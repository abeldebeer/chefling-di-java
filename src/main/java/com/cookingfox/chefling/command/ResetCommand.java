package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerHelper;
import com.cookingfox.chefling.ContainerInterface;

import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#reset()}.
 */
public class ResetCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerHelper)
     */
    public ResetCommand(ContainerHelper containerHelper) {
        super(containerHelper);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#reset()
     */
    public void reset() {
        // call destroy method for life cycle objects
        for (Map.Entry<Class, Object> entry : instances.entrySet()) {
            lifeCycleDestroy(entry.getValue());
        }

        instances.clear();
        mappings.clear();
    }

}
