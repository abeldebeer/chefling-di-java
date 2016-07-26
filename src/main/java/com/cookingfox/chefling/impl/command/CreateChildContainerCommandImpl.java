package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.CreateChildContainerCommand;

/**
 * @see CreateChildContainerCommand
 */
public class CreateChildContainerCommandImpl extends AbstractCommand implements CreateChildContainerCommand {

    public CreateChildContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public CheflingContainer createChildContainer() {
        CheflingContainer child = CommandContainerBuilder.createContainer();

        _container.addChildContainer(child);

        return child;
    }

}
