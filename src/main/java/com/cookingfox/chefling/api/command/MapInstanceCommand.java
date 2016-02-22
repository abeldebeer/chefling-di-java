package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface MapInstanceCommand {

    /**
     * Map `type` to a specific instance, which will be returned when `type` is requested. This is
     * useful when `type` has dependencies (constructor parameters) that are not resolvable by the
     * Container (e.g. `int`, `boolean`). This instance will be processed by
     * {@link Container#create(Class)}, to make sure the object is properly initialized. If a
     * mapping for `type` already exists when this method is called, an exception will be thrown.
     *
     * @param type     The type (class, interface) you want to map the instance of.
     * @param instance The instance you want to store.
     * @param <T>      Ensures the instance is of the correct type.
     * @throws ContainerException when a mapping for `type` already exists.
     */
    <T> void mapInstance(Class<T> type, T instance);

}
