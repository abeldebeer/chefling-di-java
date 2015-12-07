package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 07/12/15.
 */
public class NoopContainer implements Container {
    @Override
    public void addChild(Container container) throws ContainerException {

    }

    @Override
    public <T> T create(Class<T> type) throws ContainerException {
        return null;
    }

    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        return null;
    }

    @Override
    public boolean has(Class type) {
        return false;
    }

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {

    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {

    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {

    }

    @Override
    public void remove(Class type) throws ContainerException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void setParent(Container container) throws ContainerException {

    }
}
