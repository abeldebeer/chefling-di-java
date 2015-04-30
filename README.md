# Chefling DI for Java

Chefling is a very minimal dependency injection container written in pure Java. It does not rely on
annotations, only does constructor injection and has limited (but powerful) configuration options.

Chefling requires at minimum Java 6 or Android 2.3.

[![Build Status](https://travis-ci.org/cookingfox/chefling-di-java.svg?branch=master)](https://travis-ci.org/cookingfox/chefling-di-java)

## Download

[![Download](https://api.bintray.com/packages/cookingfox/maven/chefling-di-java/images/download.svg) ](https://bintray.com/cookingfox/maven/chefling-di-java/_latestVersion)

The distribution is hosted on [Bintray](https://bintray.com/cookingfox/maven/chefling-di-java/view).
To include the package in your projects, you can add the jCenter repository.

### Gradle

Add jCenter to your `repositories` block:

```groovy
repositories {
    jcenter()
}
```

and add the project to the `dependencies` block in your `build.gradle`:

```groovy
dependencies {
    compile 'com.cookingfox:chefling-di-java:3.1.2'
}
```

### Maven

Add jCenter to your repositories in `pom.xml` or `settings.xml`:

```xml
<repositories>
    <repository>
        <id>jcenter</id>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```

and add the project declaration to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cookingfox</groupId>
    <artifactId>chefling-di-java</artifactId>
    <version>3.1.2</version>
</dependency>
```

## Features

The Chefling [Container interface](src/main/java/com/cookingfox/chefling/ContainerInterface.java) 
defines the following methods:

- `Object get(Class type)`: Returns an instance of `type`. If a previously stored instance exists, 
it will always return that same instance. If there is no stored instance, it will create a new one 
using `create()`, and store and return that.

- `Object create(Class type)`: Creates a new instance of `type`, attempting to resolve its full 
dependency tree. The instance is not stored (that's what `get()` is for), so only use this method 
directly when you need a ___new___ instance. It uses the type mappings (from the `map...` methods) 
to create the instance. If no mapping is available, it attempts to resolve the dependencies by 
inspecting the constructor parameters. If the created instance implements the `LifeCycle` interface 
(see "Usage" -> "LifeCycle"), its `onCreate()` method will be called.

- `void mapFactory(Class type, Factory factory)`: Map `type` to a `Factory` instance (see "Usage" -> 
"Factory"), which will create an instance of `type` when it is requested (by `create()`). Which 
specific instance will be created by the Factory is up to the developer. If the Factory returns 
`null`, or a value of a different type, then an exception will be thrown by the Container.

- `void mapInstance(Class type, instance)`: Map `type` to a specific instance, which will be 
returned when `type` is requested. This is useful when `type` has dependencies (constructor 
parameters) that are not resolvable by the Container (e.g. `int`, `boolean`).

- `void mapType(Class type, Class subType)`: Map `type` to a class (`subType`) that extends it. This 
makes it possible to set a specific implementation of an interface or abstract class. When `type` is 
requested an instance of `subType` will be created.

- `boolean has(Class type)`: Returns whether a stored instance or mapping (from the `map...` 
methods) exists for `type`.

- `void remove(Class type)`: Removes a stored instance and/or mapping for `type`. If an instance 
exists and it implements `LifeCycle`, its `onDestroy()` method will be called.

- `void reset()`: Removes all stored instances and mappings. Use this method to clean up the 
Container in your application's destroy procedure. For every instance that implements `LifeCycle`, 
its `onDestroy()` method will be called.

To understand the Container internals,
[take a look at the source code](src/main/java/com/cookingfox/chefling), or
[check out the unit tests](src/test/java/com/cookingfox/chefling).

## Usage

Here's an example of the main features of the container:

```java
class First {
    String id;

    First(String id) {
        this.id = id;
    }
}

interface ISecond {}

class Second implements ISecond {}

class Third {
    First first;
    ISecond second;

    Third(First first, ISecond second) {
        this.first = first;
        this.second = second;
    }
}

// create a new DI container
Container container = new Container();

// store a specific instance
First myFirst = new First("unique id");
container.mapInstance(First.class, myFirst);

// map interface to implementation:
// the container will create an instance of Second when ISecond is requested
container.mapType(ISecond.class, Second.class);

// the container resolves the requested type (Third) and its dependencies
Third third = container.get(Third.class);

third.first.equals(myFirst); // true
third.second instanceof Second; // true
```

Explanation:

1. The `Third` class has two dependencies: `First` (class) and `ISecond` (interface).
2. By using the container's `mapInstance` method, a specific instance can be stored for a type. In 
the above example, an instance of `First` is stored, which holds a unique id.
3. The container's `mapType` method is used to map one type to a sub type. This way you can map an
interface or abstract class to a concrete implementation. In the example, the `ISecond` interface is
mapped to the `Second` class.
4. By calling the container's `get` method, the type is resolved. In this case the `Third` class
receives the explicitly mapped instance of the `First` class from step 2. The `ISecond` dependency 
is resolved with an instance of the mapped `Second` class.

### Default (static) Container instance

Some applications need to have access to the same `Container` instance across multiple processes.
This is especially common in Android, in the case of services. For these occasions a convenience
singleton method can be used: `Container.getDefault()`. This will create and return a static
instance of the container. Please be aware that if you use `getDefault()` in one place, you need to
use it everywhere, otherwise you will get different instances of the container anyway.

### LifeCycle

The [`LifeCycle` interface](src/main/java/com/cookingfox/chefling/LifeCycle.java) allows 
implementing classes to hook into the life cycle processes of the Container:

- When `Container.create()` is called and an instance of the requested type is created, it will call 
its `onCreate()` method. This will also happen for types that have been mapped using the `map...` 
methods, even `mapInstance()`. For example, if a type `Foo` is mapped to a specific instance of the 
class, and it implements the `LifeCycle` interface, then its `onCreate()` method will be called.

- The `remove()` and `reset()` methods will call the `onDestroy` method of instances that implement
the `LifeCycle` interface.

### Factory

The [`Factory` interface](src/main/java/com/cookingfox/chefling/Factory.java) defines one method 
(`create()`) that is used to create an instance of the provided type. A Factory is generally used 
when the type can not be resolved by the Container, for example when its constructor parameters are 
of a primitive type (boolean, int). Using a Factory is more efficient than mapping an instance 
directly, because it will only be called once it is requested (lazy loaded).

Example:

```java
class Bar {}

class Foo {
    Bar bar;
    String value;

    Foo(Bar bar, String value) {
        this.bar = bar;
        this.value = value;
    }
}

Container container = new Container();

// map type `Foo` to factory
container.mapFactory(Foo.class, new Factory<Foo>() {
    // the create method receives a Container instance, 
    // which can be used to get dependencies
    public Foo create(ContainerInterface container) throws ContainerException {
        return new Foo(container.get(Bar.class), "some arbitrary value");
    }
});

// ask the Container for an instance of `Foo`, which will invoke the Factory
Foo instance = container.get(Foo.class);
```

## F.A.Q.

#### _Can I use all different kinds of Java types with the container?_

No, the following types are not allowed:

- Classes in the `java.lang` package: these are considered Java language constructs. Examples:
`java.lang.String`, `java.lang.Object`, `java.lang.Exception`.
- Classes that are not public.
- Primitive types (e.g. `boolean`, `int`).
- Exceptions (extends `Throwable`).
- `enum` types.
- Annotations.
- Non-static member classes.
- Anonymous classes.

These types are not allowed because the container would not know how to resolve them automatically.
Either because there is no logical default (e.g. `boolean`, `String`), or because no instance can
be created of the type (e.g. `enum`, annotation).

#### _Java supports multiple constructors. How does Chefling decide which one to use?_

The `create()` method walks through all constructors and picks one when either:

- The constructor has no parameters. It makes it the most reasonable default.
- All constructor parameters are resolvable by the container. (see question above)

If no 'default' constructor can be picked, an exception will be thrown. This will also be the case
if the class has no `public` constructors.

#### _Does Chefling detect circular dependencies?_

Yes, an exception will be thrown. The only proper way of handling circular dependencies is to change
one of the classes, for example by introducing a setter method.

#### _Is Chefling able to solve the "robot legs" problem?_

The robot legs problem describes how two classes can depend on the same type, but 
expect a different instance of that type to be injected:

```java
interface IFoot {}

class LeftFoot implements IFoot {}

class RightFoot implements IFoot {}

class LeftLeg {
    IFoot leftFoot;

    LeftLeg (IFoot leftFoot) {
        // expects an instance of LeftFoot
        this.leftFoot = leftFoot;
    }
}

class RightLeg {
    IFoot rightFoot;

    RightLeg (IFoot rightFoot) {
        // expects an instance of RightFoot
        this.rightFoot = rightFoot;
    }
}
```

To have a dependency injection container resolve the expected dependencies, it would need to know 
which specific instance to inject, per class. This would be possible by:

- Having the ability to configure the class dependencies per constructor parameter.
- Using metadata (e.g. an `@Inject` annotation) to define which type should be injected.

Chefling does not and will not have these features. Its philosophy is to be concise and apply
convention over configuration.

#### _Why "Chefling"?_

As an analogy for dependency injection, we used cooking (we're Cooking Fox after all): when asking a
chef to prepare a meal, he/she prepares all the necessary ingredients and uses these to cook the
dish. This is basically what a DI container does: ask for an instance of a class and it will create
one, resolving its dependencies. The word "Chefling" suggests a 'small' chef, which corresponds to
the limited functionality and scope of this library.

## Copyright and license

Code and documentation copyright 2015 Cooking Fox. Code released under the Apache 2.0 license.
