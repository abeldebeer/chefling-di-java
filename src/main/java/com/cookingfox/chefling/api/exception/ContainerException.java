package com.cookingfox.chefling.api.exception;

/**
 * Base exception class for the container.
 */
public class ContainerException extends RuntimeException {

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

}
