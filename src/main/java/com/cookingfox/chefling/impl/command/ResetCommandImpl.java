package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.command.ResetCommand;
import com.cookingfox.chefling.impl.helper.Visitor;

import java.util.Map;

/**
 * @see ResetCommand
 */
class ResetCommandImpl extends AbstractCommand implements ResetCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public ResetCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void reset() {
        visitAll(_container, new Visitor() {
            @Override
            public void visit(CommandContainer container) {
                // call destroy method for life cycle objects
                for (Map.Entry<Class, Object> entry : container.instances.entrySet()) {
                    lifeCycleDestroy(entry.getValue());
                }

                container.instances.clear();
                container.mappings.clear();
            }
        });

        _container.instances.put(Container.class, _container);
        _container.instances.put(CommandContainer.class, _container);
    }

}
