package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * The Factory defines one method that is used to create an instance of type {@link <T>}. A Factory
 * is generally used when the type can not be resolved by the Container, for example when its
 * constructor parameters are of a primitive type (boolean, int). Using a Factory is more efficient
 * than mapping an instance directly, because it will only be called once it is requested.
 *
 * @param <T> Hints at the type of object the Factory should create.
 */
public interface Factory<T> {

    /**
     * Factory method that will create an instance of type {@link <T>}. The current Container
     * instance is provided, so that it can be used to request other dependencies. It is up to the
     * developer to return an instance of the correct type.
     *
     * @param container The current Container instance.
     * @return An instance of type {@link <T>}.
     * @throws ContainerException
     */
    T createInstance(Container container) throws ContainerException;

}
