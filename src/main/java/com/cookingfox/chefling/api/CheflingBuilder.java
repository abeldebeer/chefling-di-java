package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerBuilderException;

/**
 * Helper interface for streamlining the container configuration and initialization process. Added
 * {@link CheflingConfig} instances will be executed in sequence.
 */
public interface CheflingBuilder {

    /**
     * Add a container configuration.
     *
     * @param config The configuration to apply to the container.
     * @return The current Builder instance.
     * @throws ContainerBuilderException
     */
    CheflingBuilder addConfig(CheflingConfig config);

    /**
     * Creates a new container instance and applies all added configs.
     *
     * @return The created container instance.
     * @throws ContainerBuilderException
     * @see CheflingConfig
     */
    CheflingContainer buildContainer();

}
