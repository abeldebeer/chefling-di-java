package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.exception.ContainerException;

public interface MapTypeCommand {

    /**
     * Map `type` to a class (`subType`) that extends it. This makes it possible to set a specific
     * implementation of an interface or abstract class. When `type` is requested an instance of
     * `subType` will be created. If a mapping for `type` already exists when this method is called,
     * an exception will be thrown.
     *
     * @param type    The base type (class, interface), which is used when requesting an instance.
     * @param subType The type that extends / implements the base type, which is actually created.
     * @param <T>     Ensures the sub type extends the base type.
     * @throws ContainerException when a mapping for `type` already exists.
     */
    <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException;

}
