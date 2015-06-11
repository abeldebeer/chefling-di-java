package com.cookingfox.chefling.command;

import com.cookingfox.chefling.Container;
import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.RemoveTypeNotAllowedException;

import java.util.LinkedList;
import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#remove(Class)}.
 */
public class RemoveCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(Container, Map, Map)
     */
    public RemoveCommand(Container container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#remove(Class)
     */
    public void remove(Class type) throws ContainerException {
        assertNonNull(type, "type");

        Class[] protectedFromRemoval = {Container.class, ContainerInterface.class};

        // the container class and interface should not be removed from the container
        for (Class doNotRemove : protectedFromRemoval) {
            if (type.equals(doNotRemove)) {
                throw new RemoveTypeNotAllowedException(type);
            }
        }

        // call destroy method for life cycle objects
        lifeCycleDestroy(instances.get(type));

        synchronized (container) {
            // remove type from maps
            instances.remove(type);
            mappings.remove(type);

            if (!mappings.containsValue(type)) {
                return;
            }

            // Scenario: there are other mappings that are mapped to this type, e.g. when set using
            // `Container.mapType()`. Remove those mappings as well.

            LinkedList<Class> toRemoveTypes = new LinkedList<Class>();

            // find all mappings that refer to this mapping
            for (Map.Entry<Class, Object> mapping : mappings.entrySet()) {
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
