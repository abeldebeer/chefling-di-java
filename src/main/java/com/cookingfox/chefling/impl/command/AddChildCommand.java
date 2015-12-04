package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class AddChildCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.AddChildCommand {
    public AddChildCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public void addChild(Container container) throws ContainerException {
        if (container == null) {
            throw new ContainerException("Child container cannot be null");
        } else if (container.equals(_container)) {
            throw new ContainerException("Child container cannot be the Container you are adding it to");
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
