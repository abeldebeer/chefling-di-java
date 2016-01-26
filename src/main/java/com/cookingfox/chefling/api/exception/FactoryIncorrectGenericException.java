package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a Factory has an incorrect generic type.
 */
public class FactoryIncorrectGenericException extends ContainerException {

    public FactoryIncorrectGenericException(Class type, Class factoryGenericType) {
        super(String.format("The factory for '%s' has an incorrect generic type: '%s'",
                type.getName(), factoryGenericType.getName()));
    }

}
