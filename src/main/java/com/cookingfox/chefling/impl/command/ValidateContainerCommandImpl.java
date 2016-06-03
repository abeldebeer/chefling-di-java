package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.ValidateContainerCommand;
import com.cookingfox.chefling.impl.helper.CommandContainerVisitor;

/**
 * @see ValidateContainerCommand
 */
public class ValidateContainerCommandImpl extends AbstractCommand implements ValidateContainerCommand {

    public ValidateContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public synchronized void validateContainer() {
        // recursively loop through all containers
        visitAll(_container, new CommandContainerVisitor() {
            @Override
            public void visit(CommandContainer container) {
                // resolve all mappings in container
                for (Class mapping : container.mappings.keySet()) {
                    container.getInstance(mapping);
                }
            }
        });
    }

}
