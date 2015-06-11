package com.cookingfox.chefling.command;

import com.cookingfox.chefling.Container;
import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NotAnInstanceOfTypeException;

import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#mapInstance(Class, Object)}.
 */
public class MapInstanceCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(Container, Map, Map)
     */
    public MapInstanceCommand(Container container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#mapInstance(Class, Object)
     */
    public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {
        assertNonNull(type, "type");
        assertNonNull(instance, "instance");

        // validate the instance is an instance of type
        if (!type.isInstance(instance)) {
            throw new NotAnInstanceOfTypeException(type, instance);
        }

        addMapping(type, instance);
    }

}
