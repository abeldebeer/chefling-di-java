package com.cookingfox.chefling.exception;

/**
 * Thrown when a child Container is already present in the Container children.
 */
public class ChildAlreadyAddedException extends ContainerException {

    public ChildAlreadyAddedException() {
        super("The child Container is already present in the Container children");
    }

}
