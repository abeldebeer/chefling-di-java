package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Generic {@link Factory} implementation which accepts and returns an instance. Only useful for
 * testing.
 */
public class GenericInstanceFactory<T> implements Factory<T> {

    public final T instance;

    public GenericInstanceFactory(T instance) {
        this.instance = instance;
    }

    @Override
    public T createInstance(Container container) throws ContainerException {
        return instance;
    }

}
