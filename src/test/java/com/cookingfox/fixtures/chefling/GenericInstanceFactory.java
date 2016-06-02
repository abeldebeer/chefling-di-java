package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingContainer;

/**
 * Generic {@link CheflingFactory} implementation which accepts and returns an instance. Only useful for
 * testing.
 */
public class GenericInstanceFactory<T> implements CheflingFactory<T> {

    public final T instance;

    public GenericInstanceFactory(T instance) {
        this.instance = instance;
    }

    @Override
    public T createInstance(CheflingContainer container) {
        return instance;
    }

}
