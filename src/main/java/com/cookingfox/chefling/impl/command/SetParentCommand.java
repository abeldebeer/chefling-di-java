package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 07/12/15.
 */
public class SetParentCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.SetParentCommand {
    public SetParentCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public void setParent(Container container) throws ContainerException {
        if (container == null) {
            throw new NullValueNotAllowedException("Parent container can not be null");
        } else if (container.equals(_container)) {
            throw new ContainerException("Parent container can not be the same as the container it is being added to");
        } else if (_container.parent != null) {
            throw new ContainerException("Parent container is already set");
        } else if (!(container instanceof CommandContainer)) {
            throw new ContainerException("Parent container must be an instance of CommandContainer");
        }

        CommandContainer parent = (CommandContainer) container;

        checkMappingConflicts(parent);

        parent.children.add(_container);

        _container.parent = parent;
    }
}
