package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a null value is not allowed.
 */
public class NullValueNotAllowedException extends ContainerException {

    public NullValueNotAllowedException(String name) {
        super(String.format("Value for '%s' can not be null", name));
    }

}
