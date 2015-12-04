package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.impl.helper.Applier;

import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class ResetCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.ResetCommand {
    public ResetCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public void reset() {
        applyAll(_container, new Applier() {
            @Override
            public void apply(CommandContainer container) {
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
