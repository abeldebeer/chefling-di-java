package com.cookingfox.chefling.api.exception;

/**
 * Thrown when it is not allowed to remove a mapping or instance of this type.
 */
public class RemoveTypeNotAllowedException extends ContainerException {

    public RemoveTypeNotAllowedException(Class type) {
        super(String.format("It is not allowed to remove() the mappings and instances for type '%s'", type.getName()));
    }

    public RemoveTypeNotAllowedException(String message) {
        super(message);
    }

}
