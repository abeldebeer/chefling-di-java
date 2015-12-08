package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.LifeCycle;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface RemoveCommand {

    /**
     * Removes a stored instance and/or mapping for `type`. If an instance exists and it implements
     * {@link LifeCycle}, its {@link LifeCycle#dispose()} method will be called.
     *
     * @param type The type to remove the instance / mapping for.
     * @throws ContainerException when the type is not allowed to be removed.
     * @see LifeCycle#dispose()
     */
    void remove(Class type) throws ContainerException;

}
