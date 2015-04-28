package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.exception.ContainerException;

import java.util.LinkedList;
import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#get(Class)}.
 */
public class GetCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Temporary 'log' of the types that are in the process of being resolved. After the type is
     * successfully resolved, the entry is removed. This is used to detect circular dependencies.
     */
    protected final LinkedList<Class> currentlyResolving = new LinkedList<Class>();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerInterface, Map, Map)
     */
    public GetCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#get(Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) throws ContainerException {
        T instance = (T) instances.get(type);

        // an instance of this type was previously stored: return it
        if (instance != null) {
            return instance;
        }

        Object mapping = mappings.get(type);

        // this type is mapped to another type (through `mapType()`), so use the mapped type to get
        // the instance
        if (mapping instanceof Class) {
            return get((Class<T>) mapping);
        }

        synchronized (currentlyResolving) {
            // if the requested type is already being processed, it indicates a circular dependency
            if (currentlyResolving.contains(type)) {
                throw new CircularDependencyDetectedException(getDependencyTrace());
            } else {
                // store currently processed type
                currentlyResolving.add(type);
            }

            try {
                // create and store instance
                instance = container.create(type);
                instances.put(type, instance);
            } finally {
                // remove processed type
                currentlyResolving.remove(type);
            }
        }

        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Returns a trace of the dependencies.
     *
     * @return String builder, so it can be output.
     */
    protected StringBuilder getDependencyTrace() {
        StringBuilder builder = new StringBuilder();
        Class first = null;
        Class previous = null;

        for (Class current : currentlyResolving) {
            // store first entry
            if (first == null) {
                first = current;
            }

            // set previous to current entry and re-run
            if (previous == null) {
                previous = current;
                continue;
            }

            // add trace: previous > current
            builder.append(previous.getName());
            builder.append(" > ");
            builder.append(current.getName());
            builder.append("\n");

            // set previous to current
            previous = current;
        }

        // add trace: previous (last) > first
        if (previous != null) {
            builder.append(previous.getName());
            builder.append(" > ");
            builder.append(first.getName());
        }

        return builder;
    }

}
