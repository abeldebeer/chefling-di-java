package com.cookingfox.chefling.impl.command;

/**
 * Helpers for {@link CommandContainer} related tests.
 */
public final class CommandContainerTestHelper {

    /**
     * Clears the type cache, to make sure the command container is clean for every test.
     */
    public static void CLEAR_TYPE_CACHE() {
        CommandContainer.TYPE_CACHE.clear();
    }

}
