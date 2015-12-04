package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a child Container is added that is the same instance as the default Container.
 */
public class ChildCannotBeDefaultException extends ContainerException {

    public ChildCannotBeDefaultException() {
        super("The child Container cannot be the same as the default Container (through getDefault)");
    }

}
