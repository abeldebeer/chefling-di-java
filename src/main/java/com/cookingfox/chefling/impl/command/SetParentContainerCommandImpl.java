package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.SetParentContainerCommand;
import com.cookingfox.chefling.api.exception.InvalidParentContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;

/**
 * @see SetParentContainerCommand
 */
class SetParentContainerCommandImpl extends AbstractCommand implements SetParentContainerCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public SetParentContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void setParentContainer(CheflingContainer container) {
        if (container == null) {
            throw new NullValueNotAllowedException("Parent container can not be null");
        } else if (container.equals(_container)) {
            throw new InvalidParentContainerException("Parent container can not be the same " +
                    "instance as the container it is being added to");
        } else if (_container.parent != null) {
            throw new InvalidParentContainerException("Parent container is already set");
        } else if (!(container instanceof CommandContainer)) {
            throw new InvalidParentContainerException("Parent container must be an instance of " +
                    "CommandContainer");
        }

        CommandContainer parent = (CommandContainer) container;

        checkMappingConflicts(parent);

        parent.children.add(_container);

        _container.parent = parent;
    }

}
