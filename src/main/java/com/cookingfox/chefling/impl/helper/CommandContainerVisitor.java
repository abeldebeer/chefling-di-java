package com.cookingfox.chefling.impl.helper;

import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Interface for Visitor design pattern, applied to command container instances.
 */
public interface CommandContainerVisitor {

    /**
     * Perform an operation on the container instance.
     *
     * @param container The container instance.
     */
    void visit(CommandContainer container);

}
