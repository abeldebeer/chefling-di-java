package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface ValidateContainerCommand {

    /**
     * Attempts to recursively resolve all mappings of the current Container, its parents and its
     * children. This operation uses {@link CheflingContainer#getInstance(Class)}. This is useful during development
     * to validate the Container configuration. Do NOT use this method in production, since it
     * overrides the "lazy loading" ability of the container!
     *
     * @throws ContainerException when an error occurs.
     * @see CheflingContainer#getInstance(Class)
     */
    void validateContainer();

}
