package com.cookingfox.chefling.api.command;

public interface HasInstanceOrMappingCommand {

    /**
     * Returns whether a stored instance or mapping (from the `map...` methods) exists for `type`.
     *
     * @param type The type (class, interface) to check.
     * @return 'true' if a stored instance or mapping exists, 'false' otherwise.
     */
    boolean hasInstanceOrMapping(Class type);

}
