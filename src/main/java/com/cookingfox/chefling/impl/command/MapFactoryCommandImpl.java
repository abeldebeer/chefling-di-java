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
public class MapFactoryCommandImpl extends AbstractCommand implements MapFactoryCommand {

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

        Class genericType = getGenericType(factory.getClass());

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
    protected Class getGenericType(Class<? extends CheflingFactory> factoryClass) {
        // get factory's generic interfaces
        Type[] genericInterfaces = factoryClass.getGenericInterfaces();

        // no generic interfaces: factory might be in super class
        if (genericInterfaces == null || genericInterfaces.length == 0) {
            Class superClass = factoryClass.getSuperclass();

            // arrived at root class: Object
            if (Object.class.equals(superClass)) {
                return null;
            }

            // get generic type of super class
            return getGenericType(superClass);
        }

        // get first generic type
        Type firstGenericType = genericInterfaces[0];

        // not parameterized? skip
        if (!(firstGenericType instanceof ParameterizedType)) {
            return null;
        }

        // get actual type argument
        Type actual = ((ParameterizedType) firstGenericType).getActualTypeArguments()[0];

        // is TypeVariable when generic is inferred (e.g. diamond operator)
        return (actual instanceof TypeVariable) ? null : (Class) actual;
    }

}
