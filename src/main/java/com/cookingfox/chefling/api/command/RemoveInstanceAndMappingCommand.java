package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface RemoveInstanceAndMappingCommand {

    /**
     * Removes a stored instance and/or mapping for `type`. If an instance exists and it implements
     * {@link CheflingLifecycle}, its {@link CheflingLifecycle#dispose()} method will be called.
     *
     * @param type The type to remove the instance / mapping for.
     * @throws ContainerException when the type is not allowed to be removed.
     * @see CheflingLifecycle#dispose()
     */
    void removeInstanceAndMapping(Class type);

}
