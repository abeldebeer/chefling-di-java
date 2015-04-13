# Chefling DI for Java

Chefling is a very simple dependency injection container written in pure Java. It does not rely on
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
    compile 'com.cookingfox:chefling-di-java:1.0.0'
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
    <version>1.0.0</version>
</dependency>
```

## Features

The Chefling Container interface defines the following methods:

- `create(type)`: Creates a new instance of `type`, resolving its constructor dependencies.
- `get(type)`: Returns a stored instance of `type`, or creates a new one and stores that.
- `has(type)`: Returns whether an instance of `type` has been stored.
- `map(type, subType)`: Maps a class or interface to an implementation. This way, when we request
`type`, an instance of `subType` is returned.
- `set(type, instance)`: Stores an instance of `type`, overwriting a previous mapping.

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
container.set(First.class, myFirst);

// map interface to implementation:
// the container will create an instance of Second when ISecond is requested
container.map(ISecond.class, Second.class);

// the container resolves the requested type (Third) and its dependencies
Third third = container.get(Third.class);

third.first.equals(myFirst); // true
third.second instanceof Second; // true
```

Explanation:

1. The `Third` class has two dependencies: `First` (class) and `ISecond` (interface).
2. By using the container's `set` method, a specific instance can be stored for a type. In the above
example, an instance of `First` is stored, which holds a unique id.
3. The container's `map` method is used to map one type to a sub type. This way you can map an
interface or abstract class to a concrete implementation. In the example, the `ISecond` interface is
mapped to the `Second` class.
4. By calling the container's `get` method, the type is resolved. In this case the `Third` class
receives the explicitly set instance of the `First` class from step 2. The `ISecond` dependency is
resolved with an instance of the mapped `Second` class.

## F.A.Q.

#### _Can I use all different kinds of Java types with the container?_

No, the following types are not allowed:

- Classes that are not public.
- Primitive types (e.g. `boolean`, `int`).
- `enum` types.
- Annotations.
- Non-static member classes.
- Anonymous classes (`new Foo() { ... }`).
- Exceptions (classes extending `java.lang.Throwable`).
- `java.lang.Object`: it is the root of all objects.
- `java.lang.Class`: it is the root class of every type.
- `java.lang.String`: it is considered a primitive type.

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
