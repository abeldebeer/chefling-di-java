package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.MapInstanceCommand;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NotAnInstanceOfTypeException;

/**
 * @see MapInstanceCommand
 */
class MapInstanceCommandImpl extends AbstractCommand implements MapInstanceCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public MapInstanceCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
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
