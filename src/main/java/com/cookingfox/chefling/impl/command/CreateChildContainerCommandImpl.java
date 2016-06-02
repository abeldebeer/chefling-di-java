package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.CreateChildContainerCommand;

/**
 * @see CreateChildContainerCommand
 */
class CreateChildContainerCommandImpl extends AbstractCommand implements CreateChildContainerCommand {

    public CreateChildContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public CheflingContainer createChildContainer() {
        CheflingContainer child = new CommandContainer();

        _container.addChildContainer(child);

        return child;
    }

}
