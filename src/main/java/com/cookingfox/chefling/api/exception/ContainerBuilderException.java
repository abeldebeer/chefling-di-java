package com.cookingfox.chefling.api.exception;

/**
 * Thrown when an error occurs during configuration and processing of container Builder.
 */
public class ContainerBuilderException extends RuntimeException {

    public ContainerBuilderException(String message) {
        super(message);
    }

    public ContainerBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

}
