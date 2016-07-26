package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.command.ResetContainerCommand;
import com.cookingfox.chefling.impl.helper.CommandContainerVisitor;

import java.util.Map;

/**
 * @see ResetContainerCommand
 */
public class ResetContainerCommandImpl extends AbstractCommand implements ResetContainerCommand {

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
        // container listener: pre container dispose
        for (CheflingContainerListener listener : _container.containerListeners) {
            listener.preContainerDispose(_container);
        }

        // loop through all container children
        visitAll(_container, new CommandContainerVisitor() {
            @Override
            public void visit(CommandContainer container) {
                // call destroy method for life cycle objects
                for (Map.Entry<Class, Object> entry : container.instances.entrySet()) {
                    lifecycleDispose(entry.getValue());
                }

                // remove all stored instances and mappings
                container.instances.clear();
                container.mappings.clear();
            }
        });

        // container listener: post container dispose
        for (CheflingContainerListener listener : _container.containerListeners) {
            listener.postContainerDispose(_container);
        }

        // clear lifecycle references
        _container.containerListeners.clear();

        // re-initialize: set references to current container instance
        _container.instances.put(CheflingContainer.class, _container);
        _container.instances.put(CommandContainer.class, _container);
    }

}
