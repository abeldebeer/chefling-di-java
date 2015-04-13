package com.cookingfox.chefling.exception;

/**
 * Thrown when an object is not an instance of the provided type.
 */
public class NotAnInstanceOfTypeException extends ContainerException {

    public NotAnInstanceOfTypeException(Class type, Object instance) {
        super(String.format("Object '%s' is not an instance of '%s'", instance.getClass().getName(), type.getName()));
    }

}
