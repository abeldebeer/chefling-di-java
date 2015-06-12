package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerHelper;
import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.exception.ContainerException;

/**
 * Implementation of {@link ContainerInterface#mapFactory(Class, Factory)}.
 */
public class MapFactoryCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerHelper)
     */
    public MapFactoryCommand(ContainerHelper containerHelper) {
        super(containerHelper);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#mapFactory(Class, Factory)
     */
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        assertNonNull(type, "type");
        assertNonNull(factory, "factory");

        // Note: it is not possible here to check whether the factory will actually return an
        // instance of the expected type. This would require inspecting <T>, but the value of <T> is
        // not available during runtime, due to the generics type erasure.

        addMapping(type, factory);
    }

}
