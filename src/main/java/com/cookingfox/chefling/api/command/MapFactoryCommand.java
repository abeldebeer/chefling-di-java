package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface MapFactoryCommand {

    /**
     * Map `type` to a Factory, which will create an instance of `type` when it is requested (by
     * {@link CheflingContainer#createInstance(Class)}). Which specific instance will be created by the Factory is
     * up to the developer. The return value is validated by the Container: if `null` or another
     * unexpected value is returned, an exception will be thrown. If a mapping for `type` already
     * exists when this method is called, an exception will be thrown.
     *
     * @param type    The type (class, interface) of the object that will be created by the Factory.
     * @param factory A factory instance.
     * @param <T>     Ensures a factory of the expected type is passed.
     * @throws ContainerException when a mapping for `type` already exists.
     * @see CheflingFactory
     */
    <T> void mapFactory(Class<T> type, CheflingFactory<T> factory);

}
