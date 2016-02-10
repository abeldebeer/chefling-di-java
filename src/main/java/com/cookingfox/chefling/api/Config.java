package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Helper interface for modularizing Container operations. Use the {@link #apply(Container)} method
 * to configure the Container and define the initialization process.
 */
public interface Config {

    /**
     * Apply a configuration to the container.
     *
     * @param container The Container instance that is being configured.
     * @throws ContainerException
     */
    void apply(Container container) throws ContainerException;

}
