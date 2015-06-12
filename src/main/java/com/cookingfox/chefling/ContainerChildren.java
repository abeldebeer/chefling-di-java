package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ChildAlreadyAddedException;
import com.cookingfox.chefling.exception.ContainerException;

import java.util.LinkedList;
import java.util.List;

/**
 * A collection of Container instances.
 */
public class ContainerChildren {

    /**
     * Stores the child Container instances.
     */
    protected final List<Container> children = new LinkedList<Container>();

    /**
     * Add a child Container instance. Throws if it is already added.
     *
     * @param child The child Container instance to add.
     * @throws ContainerException
     */
    public void addChild(Container child) throws ContainerException {
        if (hasChild(child)) {
            throw new ChildAlreadyAddedException();
        }

        children.add(child);
    }

    /**
     * Returns the child Container that has a mapping or instance for the provided type.
     *
     * @param type The type to get the Container for.
     * @return The child Container
     */
    public Container getChildFor(Class type) {
        for (Container container : children) {
            if (container.has(type)) {
                Container nested = container.children.getChildFor(type);

                return nested == null ? container : nested;
            }
        }

        return null;
    }

    /**
     * Returns whether the provided Container has already been added.
     *
     * @param child The Container instance to check.
     * @return 'true' if the collection contains this instance.
     */
    public boolean hasChild(Container child) {
        if (children.contains(child)) {
            return true;
        }

        for (Container container : children) {
            if (container.children.hasChild(child)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether a child Container has a mapping or instance for the provided type.
     *
     * @param type The type to check.
     * @return 'true' if one of the child Containers has a configuration for this type.
     */
    public boolean hasChildFor(Class type) {
        for (Container container : children) {
            if (container.has(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a stored instance and/or mapping for `type` from the child Containers.
     *
     * @param type The type to remove the instance / mapping for.
     * @throws ContainerException
     */
    public void remove(Class type) throws ContainerException {
        for (Container container : children) {
            container.remove(type);
        }
    }

    /**
     * Removes all stored instances and mappings for the child Containers.
     */
    public void reset() {
        for (Container container : children) {
            container.reset();
        }
    }

}
