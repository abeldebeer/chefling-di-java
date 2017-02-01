# Chefling DI for Java: Change Log

## [7.1.1](../../tree/v7.1.1) (2017-02-01)

- Makes `CheflingConfigSet` varargs constructor use `#addConfig()`.
- Adds `CheflingConfigSet` example to README.

## [7.1.0](../../tree/v7.1.0) (2017-02-01)

- Introduces `CheflingConfigCollection` and `CheflingConfigSet` implementation.

## [7.0.0](../../tree/v7.0.0) (2016-07-27)

- Introduces `CheflingContainerListener` with methods for hooking into the configuration and
disposal phases of the container.
- Renames `CheflingContainer#resetContainer()` to `CheflingContainer#disposeContainer()` to better
indicate the effect of the operation.
- Renames `Chefling#builder()` to `Chefling#createBuilder()`.
- `CheflingContainer#disposeContainer()` also clears container children and parent references.
- Adds `Chefling#validateBuilderAndContainer()` helper method for validating the complete container
flow, from builder configuration to container disposal.

## [6.2.1](../../tree/v6.2.1) (2016-07-08)

- Fixes factory generic type validation with more complex factory object graph.

## [6.2.0](../../tree/v6.2.0) (2016-06-17)

- Introduces `CheflingBuilder#applyToContainer()` to be able to apply CheflingConfigs to an existing
container instance.

## [6.1.0](../../tree/v6.1.0) (2016-06-16)

- Adds `CheflingBuilder#removeConfig()` to be able to override a container configuration before it
is built.

## [6.0.1](../../tree/v6.0.1) (2016-06-03)

- Renames almost all methods to better describe the operations.
- Improves performance by caching constructors and their parameter types.

## [5.0.1](../../tree/v5.0.1) (2016-03-15)

- Improves exception messages.

## [5.0.0](../../tree/v5.0.0) (2016-02-22)

- Updates documentation.
- Adds `createChild()` and `test()` methods.
- All exceptions are now unchecked: you wouldn't be able to recover.

## [4.0.4](../../tree/v4.0.4) (2016-02-10)

- Adds Builder + Config implementation for streamlining a Container bootstrapping.

## [4.0.2](../../tree/v4.0.2) (2016-02-02)

- Fixes bug where `mapFactory()` would crash on a generic Factory implementation.

## [4.0.1](../../tree/v4.0.1) (2016-01-26)

- Now checks the generic type of Factory instance in `mapFactory()`.
- Now throws when trying to remove a mapping that has other mappings pointing to it.
- Fixes minor bug in `mapType()`.

## [4.0.0](../../tree/v4.0.0) (2015-11-12)

- Removes `Container.getDefault()`: static container instance is unnecessary.
- Introduces `Chefling.createContainer()` for providing an instance of the default Container
implementation.
- Renames `ContainerInterface` to `Container`.
- Renames `LifeCycle` methods to `initialize` and `dispose`.
- Better support for child and parent containers using a full recursive tree implementation.

## [3.2.0](../../tree/v3.2.0) (2015-06-12)

- First implementation of child container support.

## [3.1.2](../../tree/v3.1.2) (2015-04-30)

- Adds null checks for `ContainerInterface` methods: throws `NullValueNotAllowedException`.

## [3.1.1](../../tree/v3.1.1) (2015-04-29)

- Improves error message when a class has no resolvable constructors.

## [3.1.0](../../tree/v3.1.0) (2015-04-28)

- Allows mapping to another mapping, using `mapType()`. Basically adds an alias for a mapping.
- Makes `remove()` throw if type is not allowed.

## [3.0.0](../../tree/v3.0.0) (2015-04-22)

- Introduces `reset()` method, which removes all stored instances and mappings.
- Introduces `LifeCycle` interface, to hook into the Container construct / destruct process.
- Introduces Container operation commands: organizes code in separate classes.
- Renames mapping methods to `mapFactory()`, `mapInstance()` and `mapType()`.
- Introduces the `remove()` method, which removes both stored instances and mappings for a type.
- Introduces the `Factory` interface.
- Makes `has()` return whether an instance OR a mapping exists for the type.
- Makes `create()` use the mappings for a more predictable result.

## [2.0.0](../../tree/v2.0.0) (2015-04-16)

- Changes the behavior of the `set()` method: now throws instead of overwrites an existing instance.

## [1.1.0](../../tree/v1.1.0) (2015-04-16)

- Introduces `Container.getDefault()`, convenience singleton access for the Container.

## [1.0.1](../../tree/v1.0.1) (2015-04-14)

- Expands tests.

## [1.0.0](../../tree/v1.0.0) (2015-04-13)

- First version with basic functionality.
