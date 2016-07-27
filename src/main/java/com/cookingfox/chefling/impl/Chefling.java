package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.impl.command.CommandContainerBuilder;

/**
 * Helper class for managing Chefling containers.
 */
public final class Chefling {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    private Chefling() {
        // should not be instantiated
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC STATIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new Chefling Builder.
     *
     * @return A new Chefling Builder.
     */
    public static CheflingBuilder builder() {
        return new CommandContainerBuilder();
    }

    /**
     * Creates a new instance of the default Chefling {@link CheflingContainer} implementation.
     *
     * @return A new instance of the default Chefling {@link CheflingContainer} implementation.
     */
    public static CheflingContainer createContainer() {
        return CommandContainerBuilder.createContainer();
    }

    /**
     * Creates a new container using the provided builder, validates the container configuration and
     * disposes the container.
     *
     * @param builder The builder instance to create the container.
     */
    public static void validateBuilderAndContainer(CheflingBuilder builder) {
        CheflingContainer container = builder.buildContainer();
        container.validateContainer();
        container.disposeContainer();
    }

}
