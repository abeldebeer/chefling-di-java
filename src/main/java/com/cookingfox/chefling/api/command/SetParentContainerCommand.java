package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface SetParentContainerCommand {

    /**
     * Set the parent for this container. This allows for a modular approach to configuration.
     *
     * @param container The parent container to set.
     * @throws ContainerException when the parent is invalid or already added, or when the parent
     *                            contains duplicate mappings to the container it is being added to.
     * @see CheflingContainer#addChildContainer(CheflingContainer)
     */
    void setParentContainer(CheflingContainer container);

}
