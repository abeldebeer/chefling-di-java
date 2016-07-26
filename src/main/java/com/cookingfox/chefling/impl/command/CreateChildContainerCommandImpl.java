package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.CreateChildContainerCommand;

/**
 * @see CreateChildContainerCommand
 */
public class CreateChildContainerCommandImpl extends AbstractCommand implements CreateChildContainerCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public CreateChildContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public CheflingContainer createChildContainer() {
        CheflingContainer child = CommandContainerBuilder.createContainer();

        _container.addChildContainer(child);

        return child;
    }

}
