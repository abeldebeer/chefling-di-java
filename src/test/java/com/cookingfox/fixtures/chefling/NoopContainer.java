package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;

/**
 * Implementation of {@link CheflingContainer} interface without any functionality.
 */
public class NoopContainer implements CheflingContainer {

    @Override
    public void addChildContainer(CheflingContainer container) {

    }

    @Override
    public <T> T createInstance(Class<T> type) {
        return null;
    }

    @Override
    public CheflingContainer createChildContainer() {
        return null;
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return null;
    }

    @Override
    public boolean hasInstanceOrMapping(Class type) {
        return false;
    }

    @Override
    public <T> void mapFactory(Class<T> type, CheflingFactory<T> factory) {

    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) {

    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) {

    }

    @Override
    public void removeInstanceAndMapping(Class type) {

    }

    @Override
    public void disposeContainer() {

    }

    @Override
    public void setParentContainer(CheflingContainer container) {

    }

    @Override
    public void validateContainer() {

    }

}
