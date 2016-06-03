package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.MapTypeCommand;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NotASubTypeException;

/**
 * @see MapTypeCommand
 */
public class MapTypeCommandImpl extends AbstractCommand implements MapTypeCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public MapTypeCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) {
        assertNonNull(type, "type");
        assertNonNull(subType, "subType");

        // validate the sub type extends the type
        if (subType.equals(type) || !type.isAssignableFrom(subType)) {
            throw new NotASubTypeException(type, subType);
        }

        // Scenario: there can be another mapping for `subType`. If so, we will use `subType` to
        // link it to that other mapping (implicitly).
        Object mappingForSubType = _container.mappings.get(subType);

        if (mappingForSubType == null) {
            isInstantiable(subType);
        }

        addMapping(type, subType);
    }

}
