package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * @see com.cookingfox.chefling.api.command.MapFactoryCommand
 */
class MapFactoryCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.MapFactoryCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public MapFactoryCommand(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        assertNonNull(type, "type");
        assertNonNull(factory, "factory");

        // Note: it is not possible here to check whether the factory will actually return an
        // instance of the expected type. This would require inspecting <T>, but the value of <T> is
        // not available during runtime, due to the generics type erasure.

        addMapping(type, factory);
    }

}
