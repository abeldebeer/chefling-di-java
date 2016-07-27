package com.cookingfox.chefling.impl.command;

/**
 * Helpers for {@link CommandContainer} related tests.
 */
public class CommandContainerTestHelper {

    /**
     * Clears the type cache, to make sure the command container is clean for every test.
     */
    public static void CLEAR_TYPE_CACHE() {
        CreateInstanceCommandImpl.PARAM_CACHE.clear();
    }

}
