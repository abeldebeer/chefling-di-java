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
     * Instead of creating a new container, applies the added configs to the provided container
     * instance.
     *
     * @param container The container to apply the configs to.
     * @return The container.
     * @throws ContainerBuilderException
     */
    CheflingContainer applyToContainer(CheflingContainer container);

    /**
     * Creates a new container instance and applies all added configs.
     *
     * @return The created container instance.
     * @throws ContainerBuilderException
     * @see CheflingConfig
     */
    CheflingContainer buildContainer();

    /**
     * Remove a container configuration.
     *
     * @param config The configuration object to remove.
     * @return The current Builder instance.
     * @throws ContainerBuilderException
     */
    CheflingBuilder removeConfig(CheflingConfig config);

}
