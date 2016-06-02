package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a Chefling Factory returns null.
 */
public class FactoryReturnedNullException extends ContainerException {

    public FactoryReturnedNullException(Class type) {
        super(String.format("Factory for '%s' returned null", type.getName()));
    }

}
