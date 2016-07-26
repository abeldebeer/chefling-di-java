package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.command.DisposeContainerCommand;
import com.cookingfox.chefling.impl.helper.CommandContainerVisitor;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @see DisposeContainerCommand
 */
public class DisposeContainerCommandImpl extends AbstractCommand implements DisposeContainerCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public DisposeContainerCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void disposeContainer() {
        final Set<CommandContainer> allContainers = new LinkedHashSet<>();

        // collect all containers
        visitAll(_container, new CommandContainerVisitor() {
            @Override
            public void visit(CommandContainer container) {
                allContainers.add(container);
            }
        });

        // loop through all containers
        for (CommandContainer container : allContainers) {
            // temporary reference to container listeners, so real collection can be cleared
            final Set<CheflingContainerListener> containerListeners =
                    new LinkedHashSet<>(container.containerListeners);

            // notify listener: pre container dispose
            for (CheflingContainerListener listener : containerListeners) {
                listener.preContainerDispose(container);
            }

            // call destroy method for life cycle objects
            for (Object instance : container.instances.values()) {
                lifecycleDispose(instance);
            }

            // clear stored values and other references
            container.children.clear();
            container.containerListeners.clear();
            container.instances.clear();
            container.mappings.clear();
            container.parent = null;

            // notify listener: post container dispose
            for (CheflingContainerListener listener : containerListeners) {
                listener.postContainerDispose(container);
            }

            // clear temp container listeners
            containerListeners.clear();
        }
    }

}
