package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Helper interface for modularizing Container operations. Use the {@link #apply(CheflingContainer)} method
 * to configure the Container and define the initialization process.
 */
public interface CheflingConfig {

    /**
     * Apply a configuration to the container.
     *
     * @param container The Container instance that is being configured.
     * @throws ContainerException
     */
    void apply(CheflingContainer container);

}
