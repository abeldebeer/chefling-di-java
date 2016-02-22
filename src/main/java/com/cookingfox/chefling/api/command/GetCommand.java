package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface GetCommand {

    /**
     * Returns an instance of `type`. If a previously stored instance exists, it will always return
     * that same instance. If there is no stored instance, it will create a new one using
     * {@link Container#create(Class)}, and store and return that.
     *
     * @param type The type (class, interface) of the object you want to retrieve.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return Instance of `type`.
     * @throws ContainerException when an instance of the type cannot be created.
     */
    <T> T get(Class<T> type);

}
