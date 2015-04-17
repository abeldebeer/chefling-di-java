package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;

/**
 * Defines a factory method that creates an instance of a (non-resolvable) type when it is
 * requested. This is desirable when a type's dependencies are more complex than, for example, a
 * class with a one-parameter constructor.
 *
 * @param <T>
 */
public interface Factory<T> {

    /**
     * Factory method to create an instance of the type. The container is injected so that it can
     * be used to request dependencies.
     *
     * @param container The container instance that initiated this factory.
     * @return An instance of type `T`.
     * @throws ContainerException
     */
    T create(ContainerInterface container) throws ContainerException;

}
