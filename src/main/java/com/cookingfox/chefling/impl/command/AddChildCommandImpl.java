package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.command.AddChildCommand;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * @see AddChildCommand
 */
class AddChildCommandImpl extends AbstractCommand implements AddChildCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public AddChildCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addChild(Container container) {
        if (container == null) {
            throw new ContainerException("Child container cannot be null");
        } else if (container.equals(_container)) {
            throw new ContainerException("Child container can not be the same instance as the " +
                    "container it is being added to");
        } else if (!(container instanceof CommandContainer)) {
            throw new ContainerException("Child container must be an instance of CommandContainer");
        }

        CommandContainer child = (CommandContainer) container;

        if (child.parent != null) {
            throw new ContainerException("Child container has already been added");
        }

        checkMappingConflicts(child);

        child.parent = _container;

        _container.children.add(child);
    }

}
