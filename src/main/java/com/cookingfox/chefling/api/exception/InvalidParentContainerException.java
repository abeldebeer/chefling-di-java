package com.cookingfox.chefling.api.exception;

/**
 * Thrown when the provided parent container is invalid.
 */
public class InvalidParentContainerException extends ContainerException {

    public InvalidParentContainerException(String message) {
        super(message);
    }

}
