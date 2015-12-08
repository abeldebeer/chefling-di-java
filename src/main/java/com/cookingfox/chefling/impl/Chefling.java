package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Helper class for managing Chefling containers.
 */
public class Chefling {

    /**
     * Creates a new instance of the default Chefling {@link Container} implementation.
     */
    public static Container createContainer() {
        return new CommandContainer();
    }

}
