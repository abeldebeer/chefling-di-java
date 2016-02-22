# Chefling DI for Java

Chefling is a very minimal dependency injection container written in pure Java. It does not rely on
annotations, only does constructor injection and has limited (but powerful) configuration options.

Chefling requires at minimum Java 7.

[![Build Status](https://travis-ci.org/cookingfox/chefling-di-java.svg?branch=master)](https://travis-ci.org/cookingfox/chefling-di-java)

## Download

[![Download](https://api.bintray.com/packages/cookingfox/maven/chefling-di-java/images/download.svg)](https://bintray.com/cookingfox/maven/chefling-di-java/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.cookingfox/chefling-di-java/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.cookingfox/chefling-di-java)

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
    compile 'com.cookingfox:chefling-di-java:4.0.4'
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
    <version>4.0.4</version>
</dependency>
```

## Features

- Dependency injection without annotations: keeps your code clean.
- Automatic resolving of dependencies using reflection and constructor injection.
- [LifeCycle](#lifecycle) hooks for the creation and destruction phases.
- [Builder](#builder) to control configuration and initialization order.
- [Modular configuration](#container-children) support through composite containers.

## Usage

### Create a Chefling Container

The easiest way to create a Chefling Container is by doing:

```java
Container container = Chefling.createContainer();
```

This provides you with an instance of the default Container implementation.

It is also possible to use the designated Builder class:

```java
Container container = new Chefling.Builder().build();
```

See [Builder](#builder) for more information. Also see [Container children](#container-children) for
instructions on how to create and add Container child configurations.

### Ask for an instance of a type: `get(type)` and `create(type)`

There are two ways to have Chefling provide you with an instance of a type (class / interface):

- `Container#get(type)`: returns a stored instance of the type, or creates and stores a new 
instance using:

- `Container#create(type)`: always creates a new instance that is NOT stored. This method 
should only be called directly when you are absolutely sure you need a new instance, which is 
usually not the case.

When `Container#create(type)` is called, Chefling attempts to resolve all dependencies (constructor
arguments) of the provided type. See the
[F.A.Q.](#can-i-use-all-different-kinds-of-java-types-with-the-container) for information on which 
types can and can not be resolved by Chefling. If the type implements the `LifeCycle` interface, its 
`initialize()` method will be called by the `create(type)` method. See [LifeCycle](#lifecycle) for 
more information.

### Configure the Container: `map*()` methods

You can configure the Container, so that when you ask for a type, the Container provides you with a
specific instance or implementation.

#### Use a specific instance: `mapInstance(type, instance)`

Mapping a specific instance of a type is useful when it has dependencies on unresolvable types, such
as `String` or `Boolean`:

```java
// provide the specific instance
container.mapInstance(MyClass.class, new MyClass("some value", true));

// `resolved` is the provided instance
MyClass resolved = container.get(MyClass.class);
```

#### Use a specific implementation: `mapType(type, subType)`

Chefling can not create an instance of an interface or abstract class, so you will need to define 
which implementation you want to use:

```java
// map the interface to a specific implementation
container.mapType(MyInterface.class, MyImplementation.class);

// `resolved` is an instance of MyImplementation
MyInterface resolved = container.get(MyInterface.class);
```

#### Use a factory: `mapFactory(type, factory)`

If a type has dependencies that are both resolvable and unresolvable, you can map a `Factory`
implementation:

```java
// map the type to a Factory
container.mapFactory(MyInterface.class, new Factory<MyInterface>() {
    @Override
    public MyInterface createInstance(Container container) {
        return new MyImplementation("some value", container.get(OtherType.class));
    }
});

// `resolved` is the result of the Factory method
MyInterface resolved = container.get(MyInterface.class);
```

If the `Factory` returns null or something that is not an instance of the expected type, an 
exception will be thrown.

### LifeCycle

The [`LifeCycle` interface](src/main/java/com/cookingfox/chefling/api/LifeCycle.java) allows 
implementing classes to hook into the life cycle processes of the Container:

- When `Container#create(type)` is called and an instance of the requested type is created, it will 
call its `initialize()` method. This will also happen for types that have been mapped using the 
`map...` methods, even `mapInstance()`. For example, if a type `Foo` is mapped to a specific 
instance of the class, and it implements the `LifeCycle` interface, then its `initialize()` method 
will be called.

- The `remove()` and `reset()` methods will call the `dispose()` method of instances that implement
the `LifeCycle` interface.

### Builder

When your application grows, the Chefling Container configuration grows as well. You'll start 
noticing different types of configuration, such as libraries, your application domain and the 
initialization of the application. The Container Builder allows you to modularize your Chefling
configuration into `Config` instances:

```java
Config libraryConfig = new Config() {
    @Override
    public void apply(Container container) {
        // configure container with library dependencies
        container.mapType(IMyLib.class, MyLibImpl.class);
    }
};

Config initAppConfig = new Config() {
    @Override
    public void apply(Container container) {
        // initialize application components
        container.get(MyViewController.class);
    }
};

Container container = new Chefling.Builder()
    .add(libraryConfig)
    .add(initAppConfig)
    .build();
```

The Builder applies the `Config` instances in the order they were added. Of course, you can define
your own classes that implement this interface for the desired level of modularity.

### Container children

Chefling supports modularizing Container configurations through a composite pattern:

```java
// example module configuration
Container moduleContainer = Chefling.createContainer();
moduleContainer.mapType(IModule.class, ModuleImplementation.class);

// app container configuration
Container appContainer = Chefling.createContainer();
appContainer.mapType(IApp.class, AppImpl.class);

// add module configuration to app container
appContainer.addChild(moduleContainer);
```

This means that when the `appContainer` asks for `IModule`, it will receive a `ModuleImplementation`
instance.

Note that when a child Container is added which contains a mapping or instance for a type that is 
already present in the Container it is added to, an exception will be thrown:

```java
Container moduleContainer = Chefling.createContainer();
moduleContainer.mapType(IModule.class, ModuleImplementation.class);

// container configuration with same mapping
Container appContainer = Chefling.createContainer();
appContainer.mapType(IModule.class, OtherModuleImplementation.class);

// exception: mapping for "IModule" already exists
appContainer.addChild(moduleContainer);
```

It is also possible to do the inverse: set the parent of a Container, using `setParent(Container)`.

There's a helper method available for creating AND adding a Container child: `createChild()`.

### Testing the configuration

Since dependencies are resolved at runtime, it can be useful to make sure your configuration is 
correct during the development phase. To have Chefling resolve all mappings, use the 
`Container#test()` method. This will bring any configuration issues to light.

__WARNING: Make sure to remove this call for production builds!__

## F.A.Q.

#### _Can I use all different kinds of Java types with the container?_

No, the following types are not allowed:

- Classes in the `java.*` and `javax.*` packages: these are considered Java language constructs. 
Examples: `java.lang.String`, `java.lang.Exception`, `java.util.LinkedList`.
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

We used cooking as an analogy for dependency injection (we're Cooking Fox after all): when asking a
chef to prepare a meal, he/she prepares all the necessary ingredients and uses these to cook the
dish. This is basically what a DI container does: ask for an instance of a class and it will create
one, resolving its dependencies. The word "Chefling" suggests a 'small' chef, which corresponds to
the limited functionality and scope of this library.

## Copyright and license

Code and documentation copyright 2016 Cooking Fox. Code released under the Apache 2.0 license.
