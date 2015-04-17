package com.cookingfox.chefling.exception;

/**
 * Thrown when a Factory returns null.
 */
public class FactoryReturnedNullException extends ContainerException {

    public FactoryReturnedNullException(Class type) {
        super(String.format("Factory for '%s' returned null", type.getName()));
    }

}
