package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;

public interface HasInstanceOrMappingCommand {

    /**
     * Returns whether a stored instance (from {@link CheflingContainer#getInstance(Class)}) or
     * mapping (from the {@link CheflingContainer} `map...` methods) exists for `type`.
     *
     * @param type The type (class, interface) to check.
     * @return 'true' if a stored instance or mapping exists, 'false' otherwise.
     */
    boolean hasInstanceOrMapping(Class type);

}
