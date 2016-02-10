package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerBuilderException;

/**
 * Helper interface for streamlining the Container configuration and initialization process. Added
 * {@link Config} instances will be executed in sequence.
 */
public interface Builder {

    /**
     * Add a Container configuration.
     *
     * @param config The configuration to apply to the Container.
     * @return The current Builder instance.
     * @throws ContainerBuilderException
     */
    Builder add(Config config);

    /**
     * Creates a new Container instance and applies all added configs.
     *
     * @return The created Container instance.
     * @throws ContainerBuilderException
     */
    Container build();

}
