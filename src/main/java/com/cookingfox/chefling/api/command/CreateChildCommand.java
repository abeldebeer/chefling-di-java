package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;

public interface CreateChildCommand {

    /**
     * Creates a new Container and adds it as a child.
     *
     * @return The created child Container.
     * @see Container#addChild(Container)
     */
    Container createChild();

}
