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
     * @return The current builder instance.
     * @throws ContainerBuilderException when the config is invalid.
     */
    CheflingBuilder addConfig(CheflingConfig config);

    /**
     * Add a container listener.
     *
     * @param listener The listener to add.
     * @return The current builder instance.
     */
    CheflingBuilder addContainerListener(CheflingContainerListener listener);

    /**
     * Instead of creating a new container, applies the added configs to the provided container
     * instance.
     *
     * @param container The container to apply the configs to.
     * @return The container the configs were applied to.
     * @throws ContainerBuilderException when an error occurs.
     */
    CheflingContainer applyToContainer(CheflingContainer container);

    /**
     * Creates a new container instance and applies all added configs.
     *
     * @return The created container instance.
     * @throws ContainerBuilderException when an error occurs.
     * @see CheflingConfig
     */
    CheflingContainer buildContainer();

    /**
     * Remove a container configuration.
     *
     * @param config The configuration object to remove.
     * @return The current builder instance.
     * @throws ContainerBuilderException when the config is invalid.
     */
    CheflingBuilder removeConfig(CheflingConfig config);

    /**
     * Remove a container listener.
     *
     * @param listener The listener to remove.
     * @return The current builder instance.
     */
    CheflingBuilder removeContainerListener(CheflingContainerListener listener);

}
