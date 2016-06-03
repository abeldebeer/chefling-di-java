package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.AddChildContainerCommand;
import com.cookingfox.chefling.api.exception.InvalidChildContainerException;

/**
 * @see AddChildContainerCommand
 */
public class AddChildContainerCommandImpl extends AbstractCommand implements AddChildContainerCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public AddChildContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addChildContainer(CheflingContainer container) {
        if (container == null) {
            throw new InvalidChildContainerException("Child container cannot be null");
        } else if (container.equals(_container)) {
            throw new InvalidChildContainerException("Child container can not be the same " +
                    "instance as the container it is being added to");
        } else if (!(container instanceof CommandContainer)) {
            throw new InvalidChildContainerException("Child container must be an instance of " +
                    "CommandContainer");
        }

        CommandContainer child = (CommandContainer) container;

        if (child.parent != null) {
            throw new InvalidChildContainerException("Child container has already been added");
        }

        checkMappingConflicts(child);

        child.parent = _container;

        _container.children.add(child);
    }

}
