package com.cookingfox.chefling.exception;

/**
 * Thrown when the provided type can not be instantiated, e.g. when it is an interface.
 */
public class TypeNotInstantiableException extends TypeNotAllowedException {

    public TypeNotInstantiableException(Class type, String reason) {
        super(String.format("Type '%s' is not instantiable, because %s", type.getName(), reason));
    }

}
