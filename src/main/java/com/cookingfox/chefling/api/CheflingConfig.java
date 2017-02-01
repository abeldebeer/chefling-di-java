package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerBuilderException;

/**
 * Helper interface for streamlining the container configuration process. Use the
 * {@link #apply(CheflingContainer)} method to configure the container and define the initialization
 * process.
 */
public interface CheflingConfig {

    /**
     * Apply a configuration to the container.
     *
     * @param container The container instance that is being configured.
     * @throws ContainerBuilderException when an error occurs.
     */
    void apply(CheflingContainer container);

}
