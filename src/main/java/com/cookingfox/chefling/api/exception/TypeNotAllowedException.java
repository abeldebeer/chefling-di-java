package com.cookingfox.chefling.api.exception;

/**
 * Thrown when this type is not allowed to be used in the container, e.g. an enum.
 */
public class TypeNotAllowedException extends ContainerException {

    public TypeNotAllowedException(String message) {
        super(message);
    }

    public TypeNotAllowedException(Class type, String reason) {
        super(String.format("Type '%s' is not allowed, because it is %s", type.getName(), reason));
    }

}
