package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.command.CreateChildCommand;

/**
 * @see CreateChildCommand
 */
class CreateChildCommandImpl extends AbstractCommand implements CreateChildCommand {

    public CreateChildCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public Container createChild() {
        Container child = new CommandContainer();

        _container.addChild(child);

        return child;
    }

}
