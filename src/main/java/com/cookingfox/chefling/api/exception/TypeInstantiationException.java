package com.cookingfox.chefling.api.exception;

import java.lang.reflect.InvocationTargetException;

/**
 * Thrown when a type could not be instantiated, e.g. when the constructor throws.
 */
public class TypeInstantiationException extends ContainerException {

    public TypeInstantiationException(Class type, Throwable cause) {
        super(String.format("Type '%s' could not be instantiated: '%s'", type.getName(),
                createCauseReasonMessage(cause)), cause);
    }

    /**
     * Creates a proper reason message for the cause of the exception.
     */
    private static String createCauseReasonMessage(Throwable cause) {
        if (cause instanceof InvocationTargetException) {
            return cause.getCause().getMessage();
        }

        return cause.getMessage();
    }

}
