package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;

/**
 * Implementation of {@link Container} interface without any functionality.
 */
public class NoopContainer implements Container {

    @Override
    public void addChild(Container container) {

    }

    @Override
    public <T> T create(Class<T> type) {
        return null;
    }

    @Override
    public Container createChild() {
        return null;
    }

    @Override
    public <T> T get(Class<T> type) {
        return null;
    }

    @Override
    public boolean has(Class type) {
        return false;
    }

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) {

    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) {

    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) {

    }

    @Override
    public void remove(Class type) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void setParent(Container container) {

    }

    @Override
    public void test() {

    }

}
