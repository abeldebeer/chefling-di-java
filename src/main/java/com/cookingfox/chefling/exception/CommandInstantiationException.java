package com.cookingfox.chefling.exception;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public class CommandInstantiationException extends ContainerException {

    public CommandInstantiationException(Class commandClass, Exception e) {
        super(String.format("Could not create an instance of '%s': %s",
                commandClass.getSimpleName(), e.getMessage()));
    }

}
