package com.cookingfox.chefling.command;

import com.cookingfox.chefling.Container;
import com.cookingfox.chefling.ContainerHelper;
import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.ChildCannotBeSelfException;
import com.cookingfox.chefling.exception.ChildConfigurationConflictException;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NullValueNotAllowedException;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link Container#addChild(Container)}.
 */
public class AddChildCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerHelper)
     */
    public AddChildCommand(ContainerHelper containerHelper) {
        super(containerHelper);
    }

    /**
     * Adds a child Container, which contains its own unique configuration.
     *
     * @param child The child Container.
     * @throws ContainerException
     */
    public void addChild(Container child) throws ContainerException {
        if (child == null) {
            throw new NullValueNotAllowedException("child");
        } else if (child == container) {
            throw new ChildCannotBeSelfException();
        }

        // collect all types for the current instances and mappings
        Set<Class> allTypes = new HashSet<Class>();
        allTypes.addAll(instances.keySet());
        allTypes.addAll(mappings.keySet());
        allTypes.remove(Container.class);
        allTypes.remove(ContainerInterface.class);

        // check whether the child Container has these types in its configuration
        for (Class type : allTypes) {
            if (child.has(type)) {
                throw new ChildConfigurationConflictException(type);
            }
        }

        children.addChild(child);
    }

}
