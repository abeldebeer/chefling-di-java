package com.cookingfox.chefling.exception;

/**
 * Thrown when an instance of a type is already stored and is attempted to be replaced.
 */
public class ReplaceInstanceNotAllowedException extends ContainerException {

    public ReplaceInstanceNotAllowedException(Class type, Object storedInstance, Object newInstance) {
        super(String.format("An instance of %s is already stored (%s) and is not allowed to be " +
                "replaced by a new instance (%s)", type.getName(), storedInstance, newInstance));
    }

}
