package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NotASubTypeException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class MapTypeCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.MapTypeCommand {
    public MapTypeCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {
        assertNonNull(type, "type");
        assertNonNull(subType, "subType");

        // validate the sub type extends the type
        if (subType.equals(type) || !type.isAssignableFrom(subType)) {
            throw new NotASubTypeException(type, subType);
        }

        // Scenario: there can be another mapping for `subType`. If so, we will use `subType` to
        // link it to that other mapping.
        Object mappingForSubType = _container.mappings.get(subType);

        if (mappingForSubType == null || !type.isAssignableFrom((Class) mappingForSubType)) {
            isInstantiable(subType);
        }

        addMapping(type, subType);
    }
}
