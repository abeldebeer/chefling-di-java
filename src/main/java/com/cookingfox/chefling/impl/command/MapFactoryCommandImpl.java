package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.command.MapFactoryCommand;
import com.cookingfox.chefling.api.exception.FactoryIncorrectGenericException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

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
    public <T> void mapFactory(Class<T> type, CheflingFactory<T> factory) {
        assertNonNull(type, "type");
        assertNonNull(factory, "factory");

        final Class<T> genericType = getGenericClass(factory);

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
    @SuppressWarnings("unchecked")
    protected <T> Class<T> getGenericClass(CheflingFactory<T> factory) {
        // get first generic type
        final Type generic = factory.getClass().getGenericInterfaces()[0];

        // not parameterized? skip
        if (!(generic instanceof ParameterizedType)) {
            return null;
        }

        // get actual type argument
        final Type actual = ((ParameterizedType) generic).getActualTypeArguments()[0];

        // is TypeVariable when generic is inferred (e.g. diamond operator)
        return (actual instanceof TypeVariable) ? null : (Class<T>) actual;
    }

}
