package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.ResetContainerCommand;
import com.cookingfox.chefling.impl.helper.CommandContainerVisitor;

import java.util.Map;

/**
 * @see ResetContainerCommand
 */
class ResetContainerCommandImpl extends AbstractCommand implements ResetContainerCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public ResetContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void resetContainer() {
        visitAll(_container, new CommandContainerVisitor() {
            @Override
            public void visit(CommandContainer container) {
                // call destroy method for life cycle objects
                for (Map.Entry<Class, Object> entry : container.instances.entrySet()) {
                    lifecycleDispose(entry.getValue());
                }

                container.instances.clear();
                container.mappings.clear();
            }
        });

        _container.instances.put(CheflingContainer.class, _container);
        _container.instances.put(CommandContainer.class, _container);
    }

}
