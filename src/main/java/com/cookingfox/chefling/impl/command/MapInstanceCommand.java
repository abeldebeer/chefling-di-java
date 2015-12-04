package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NotAnInstanceOfTypeException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class MapInstanceCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.MapInstanceCommand {
    public MapInstanceCommand(CommandContainer container) {
        super(container);
    }

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
