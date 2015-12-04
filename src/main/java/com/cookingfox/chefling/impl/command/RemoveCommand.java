package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class RemoveCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.RemoveCommand {
    public RemoveCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public void remove(Class type) throws ContainerException {
        assertNonNull(type, "type");

        Class[] protectedFromRemoval = {Container.class, CommandContainer.class};

        // the container class and interface should not be removed from the container
        for (Class doNotRemove : protectedFromRemoval) {
            if (type.equals(doNotRemove)) {
                throw new RemoveTypeNotAllowedException(type);
            }
        }

        // call destroy method for life cycle objects
        lifeCycleDestroy(_container.instances.get(type));

        synchronized (_container) {
            // remove type from maps
            _container.instances.remove(type);
            _container.mappings.remove(type);

            if (!_container.mappings.containsValue(type)) {
                return;
            }

            // Scenario: there are other mappings that are mapped to this type, e.g. when set using
            // `Container.mapType()`. Remove those mappings as well.

            LinkedList<Class> toRemoveTypes = new LinkedList<Class>();

            // find all mappings that refer to this mapping
            for (Map.Entry<Class, Object> mapping : _container.mappings.entrySet()) {
                if (mapping.getValue().equals(type)) {
                    toRemoveTypes.add(mapping.getKey());
                }
            }

            // remove the types
            for (Class toRemoveType : toRemoveTypes) {
                remove(toRemoveType);
            }
        }
    }
}
