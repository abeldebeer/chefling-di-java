package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * The factory defines one method that is used to create an instance of type {@link <T>}. A factory
 * is generally used when the type can not be resolved by the container, for example when its
 * constructor parameters are of a primitive type (boolean, int). Using a factory is more efficient
 * than mapping an instance directly, because it will only be called once it is requested.
 *
 * @param <T> Hints at the type of object the factory should create.
 */
public interface CheflingFactory<T> {

    /**
     * Factory method that will create an instance of type {@link <T>}. The current container
     * instance is provided, so that it can be used to request other dependencies. It is up to the
     * developer to return an instance of the correct type.
     *
     * @param container The current container instance.
     * @return An instance of type {@link <T>}.
     * @throws ContainerException
     */
    T createInstance(CheflingContainer container);

}
