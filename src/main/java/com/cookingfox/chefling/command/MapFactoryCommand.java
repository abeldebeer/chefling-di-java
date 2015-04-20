package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.TypeMappingAlreadyExistsException;

import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public class MapFactoryCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public MapFactoryCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        isAllowed(type);

        if (container.has(type)) {
            throw new TypeMappingAlreadyExistsException(type);
        }

        mappings.put(type, factory);
    }

}
