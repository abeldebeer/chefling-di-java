package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerBuilderException;

/**
 * Manages a collection of {@link CheflingConfig} objects.
 */
public interface CheflingConfigCollection {

    /**
     * Add a container configuration.
     *
     * @param config The configuration to apply to the container.
     * @return The current builder instance.
     * @throws ContainerBuilderException when the config is invalid.
     */
    CheflingConfigCollection addConfig(CheflingConfig config);

    /**
     * Returns whether the provided configuration object has already been added.
     *
     * @param config The configuration object to check.
     * @return Whether the provided configuration object has already been added.
     */
    boolean containsConfig(CheflingConfig config);

    /**
     * Remove a container configuration.
     *
     * @param config The configuration object to remove.
     * @return The current builder instance.
     * @throws ContainerBuilderException when the config is invalid.
     */
    CheflingConfigCollection removeConfig(CheflingConfig config);

}
