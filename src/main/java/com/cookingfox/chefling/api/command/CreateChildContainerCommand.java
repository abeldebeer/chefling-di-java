package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;

public interface CreateChildContainerCommand {

    /**
     * Creates a new container and adds it as a child.
     *
     * @return The created child container.
     * @see CheflingContainer#addChildContainer(CheflingContainer)
     */
    CheflingContainer createChildContainer();

}
