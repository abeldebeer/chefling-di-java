package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerHelper;
import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NotAnInstanceOfTypeException;

/**
 * Implementation of {@link ContainerInterface#mapInstance(Class, Object)}.
 */
public class MapInstanceCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerHelper)
     */
    public MapInstanceCommand(ContainerHelper containerHelper) {
        super(containerHelper);
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
