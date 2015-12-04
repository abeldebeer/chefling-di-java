package com.cookingfox.chefling.api.exception;

/**
 * Thrown when an previously stored Container instance is being added to a ContainerSet.
 */
public class ContainerAlreadyAddedToSetException extends ContainerException {

    public ContainerAlreadyAddedToSetException() {
        super("The Container instance is already present in the Container set");
    }

}
