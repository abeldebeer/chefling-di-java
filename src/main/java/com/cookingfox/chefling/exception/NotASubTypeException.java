package com.cookingfox.chefling.exception;

/**
 * Thrown when a type is not a sub type of another type, e.g. a class is expected to implement an
 * interface, but it does not.
 */
public class NotASubTypeException extends ContainerException {

    public NotASubTypeException(Class type, Class subType) {
        super(String.format("Type '%s' is not a sub type of '%s'",
                subType.getName(), type.getName()));
    }

}
