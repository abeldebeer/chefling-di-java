package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a type could not be instantiated, e.g. when type is an abstract class.
 */
public class TypeInstantiationException extends ContainerException {

    public TypeInstantiationException(Class type, Throwable cause) {
        super(String.format("Type '%s' could not be instantiated: %s",
                type.getName(),
                cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName()));
    }

}
