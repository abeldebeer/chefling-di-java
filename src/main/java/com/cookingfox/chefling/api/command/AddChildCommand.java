package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface AddChildCommand {

    /**
     * Add a child container. This allows for a modular approach to configuration.
     *
     * @param container The child container to add.
     * @throws ContainerException when the child is invalid or already added, or when the child
     *                            contains duplicate mappings to the container it is being added to.
     * @see Container#setParent(Container)
     */
    void addChild(Container container) throws ContainerException;

}
