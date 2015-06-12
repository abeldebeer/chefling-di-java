package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerAlreadyAddedToSetException;
import com.cookingfox.chefling.exception.ContainerException;

import java.util.HashSet;
import java.util.Set;

/**
 * A set of Container instances, with helper methods to get a Container for a specific type.
 */
public class ContainerSet {

    /**
     * A collection of unique container instances.
     */
    protected final Set<Container> containers = new HashSet<Container>();

    /**
     * Add a Container instance to the set. Throws if it was already added.
     *
     * @param container The Container instance to add.
     * @throws ContainerException
     */
    public synchronized void add(Container container) throws ContainerException {
        if (has(container)) {
            throw new ContainerAlreadyAddedToSetException();
        }

        containers.add(container);
    }

    /**
     * Returns the Container instance that has a mapping or instance for the provided type.
     *
     * @param type The type to get the Container for.
     * @return The Container instance, or `null` if there is none.
     */
    public Container getForType(Class type) {
        for (Container container : containers) {
            if (container.has(type)) {
                return container;
            }
        }

        return null;
    }

    /**
     * Returns whether the provided Container instance has already been added.
     *
     * @param container The Container instance to check.
     * @return 'true' if the collection contains this instance.
     */
    public boolean has(Container container) {
        // provided container is part of this set?
        if (containers.contains(container)) {
            return true;
        }

        // provided container is child of container set?
        for (Container aContainer : containers) {
            if (aContainer.hasChild(container)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether a stored Container instance has a configuration for the provided type.
     *
     * @param type The type to check.
     * @return 'true' if one of the stored Container instances has a configuration for this type.
     */
    public boolean hasForType(Class type) {
        for (Container container : containers) {
            if (container.has(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes a stored instance and/or mapping for `type` from the stored Containers.
     *
     * @param type The type to remove the instance / mapping for.
     * @throws ContainerException
     */
    public void remove(Class type) throws ContainerException {
        for (Container container : containers) {
            container.remove(type);
        }
    }

    /**
     * Removes all stored instances and mappings for the stored Containers.
     */
    public void reset() {
        for (Container container : containers) {
            container.reset();
        }
    }

}
