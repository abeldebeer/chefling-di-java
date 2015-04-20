package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NotAnInstanceOfTypeException;
import com.cookingfox.chefling.exception.ReplaceInstanceNotAllowedException;
import com.cookingfox.chefling.exception.TypeMappingAlreadyExistsException;

import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public class MapInstanceCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public MapInstanceCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {
        // validate the instance is an instance of type
        if (!type.isInstance(instance)) {
            throw new NotAnInstanceOfTypeException(type, instance);
        }

        // if a mapping for type exists, throw
        if (mappings.containsKey(type)) {
            throw new TypeMappingAlreadyExistsException(type);
        }

        isAllowed(type);

        // if an instance of type is already stored, throw
        if (instances.containsKey(type)) {
            throw new ReplaceInstanceNotAllowedException(type, instances.get(type), instance);
        }

        instances.put(type, instance);
    }

}
