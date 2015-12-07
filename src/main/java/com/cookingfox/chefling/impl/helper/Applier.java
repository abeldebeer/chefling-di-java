package com.cookingfox.chefling.impl.helper;

import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Interface for applying a user-specified operation to a collection of {@link CommandContainer}
 * instances.
 */
public interface Applier {

    /**
     * Apply an operation to the container.
     *
     * @param container The current container instance.
     */
    void apply(CommandContainer container);

}
