package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingContainer;

public interface CreateChildContainerCommand {

    /**
     * Creates a new Container and adds it as a child.
     *
     * @return The created child Container.
     * @see CheflingContainer#addChildContainer(CheflingContainer)
     */
    CheflingContainer createChildContainer();

}
