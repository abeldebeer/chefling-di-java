package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a type mapping already exists.
 */
public class TypeMappingAlreadyExistsException extends ContainerException {

    public TypeMappingAlreadyExistsException(Class type) {
        super(String.format("A type mapping for '%s' already exists", type.getName()));
    }

}
