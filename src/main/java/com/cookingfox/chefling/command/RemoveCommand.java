package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;

import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#remove(Class)}.
 */
public class RemoveCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerInterface, Map, Map)
     */
    public RemoveCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#remove(Class)
     */
    public void remove(Class type) throws ContainerException {
        isAllowed(type);

        // call destroy method for life cycle objects
        lifeCycleDestroy(instances.get(type));

        instances.remove(type);
        mappings.remove(type);
    }

}
