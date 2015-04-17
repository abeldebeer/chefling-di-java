package com.cookingfox.chefling.exception;

/**
 * Thrown when a Factory returns an unexpected value.
 */
public class FactoryReturnedUnexpectedValueException extends ContainerException {

    public FactoryReturnedUnexpectedValueException(Class type, Object value) {
        super(String.format("Factory for type '%s' returned an unexpected value: '%s' (%s)",
                type, value, value.getClass().getName()));
    }

}
