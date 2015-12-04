package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a child Container is added that is the same instance as the Container it is being
 * added to.
 */
public class ChildCannotBeSelfException extends ContainerException {

    public ChildCannotBeSelfException() {
        super("The child Container cannot be the same as the Container you are adding it to");
    }

}
