package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NotASubTypeException;

import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#mapType(Class, Class)}.
 */
public class MapTypeCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerInterface, Map, Map)
     */
    public MapTypeCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#mapType(Class, Class)
     */
    public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {
        // validate the sub type extends the type
        if (subType.equals(type) || !type.isAssignableFrom(subType)) {
            throw new NotASubTypeException(type, subType);
        }

        // Scenario: there can be another mapping for `subtype`. If so, we will use `subType` to
        // link it to that other mapping.
        Object mappingForSubType = mappings.get(subType);

        if (mappingForSubType == null || !type.isAssignableFrom((Class) mappingForSubType)) {
            isInstantiable(subType);
        }

        addMapping(type, subType);
    }

}