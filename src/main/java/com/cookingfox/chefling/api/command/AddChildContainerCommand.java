package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface AddChildContainerCommand {

    /**
     * Add a child container. This allows for a modular approach to configuration.
     *
     * @param container The child container to add.
     * @throws ContainerException when the child is invalid or already added, or when the child
     *                            contains duplicate mappings to the container it is being added to.
     * @see CheflingContainer#setParentContainer(CheflingContainer)
     */
    void addChildContainer(CheflingContainer container);

}
