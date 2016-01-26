package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.command.MapFactoryCommand;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.FactoryIncorrectGenericException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @see MapFactoryCommand
 */
class MapFactoryCommandImpl extends AbstractCommand implements MapFactoryCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public MapFactoryCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        assertNonNull(type, "type");
        assertNonNull(factory, "factory");

        final Class genericType = getGenericClass(factory);

        // validate generic type, if available
        if (genericType != null && !type.isAssignableFrom(genericType)) {
            throw new FactoryIncorrectGenericException(type, genericType);
        }

        addMapping(type, factory);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Attempt to extract the generic ("parameterized") type from the factory.
     */
    protected Class getGenericClass(Factory factory) {
        for (Type i : factory.getClass().getGenericInterfaces()) {
            if (i instanceof ParameterizedType) {
                final ParameterizedType parameterized = (ParameterizedType) i;

                if (parameterized.getRawType().equals(Factory.class)) {
                    return (Class) parameterized.getActualTypeArguments()[0];
                }
            }
        }

        return null;
    }

}
