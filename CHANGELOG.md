# Chefling DI for Java: Change Log

## [3.1.0](../tree/v3.1.0) (2015-04-28)

- Allows mapping to another mapping, using `mapType()`. Basically adds an alias for a mapping.
- Makes `remove()` throw if type is not allowed.

## [3.0.0](../tree/v3.0.0) (2015-04-22)

- Introduces `reset()` method, which removes all stored instances and mappings.
- Introduces `LifeCycle` interface, to hook into the Container construct / destruct process.
- Introduces Container operation commands: organizes code in separate classes.
- Renames mapping methods to `mapFactory()`, `mapInstance()` and `mapType()`.
- Introduces the `remove()` method, which removes both stored instances and mappings for a type.
- Introduces the `Factory` interface.
- Makes `has()` return whether an instance OR a mapping exists for the type.
- Makes `create()` use the mappings for a more predictable result.

## [2.0.0](../tree/v2.0.0) (2015-04-16)

- Changes the behavior of the `set()` method: now throws instead of overwrites an existing instance.

## [1.1.0](../tree/v1.1.0) (2015-04-16)

- Introduces `Container.getDefault()`, convenience singleton access for the Container.

## [1.0.1](../tree/v1.0.1) (2015-04-14)

- Expands tests.

## [1.0.0](../tree/v1.0.0) (2015-04-13)

- First version with basic functionality.
