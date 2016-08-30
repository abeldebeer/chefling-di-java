package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.GetInstanceCommand;
import com.cookingfox.chefling.api.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.util.LinkedList;

/**
 * @see GetInstanceCommand
 */
public class GetInstanceCommandImpl extends AbstractCommand implements GetInstanceCommand {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Temporary 'log' of the types that are in the process of being resolved. After the type is
     * successfully resolved, the entry is removed. This is used to detect circular dependencies.
     */
    protected final LinkedList<Class> currentlyResolving = new LinkedList<>();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public GetInstanceCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> T getInstance(Class<T> type) {
        assertNonNull(type, "type");

        CommandContainer owner = findOneWithInstanceOrMapping(_container, type);

        return owner == null ? createInstance(type) : useOwnerInstanceOrMapping(owner, type);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Attempts to create an instance of the type.
     *
     * @param type The type to create an instance of.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return The created instance.
     * @throws ContainerException when the instance could not be created.
     */
    protected <T> T createInstance(Class<T> type) {
        T instance;

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
                instance = _container.createInstance(type);
                _container.instances.put(type, instance);
            } finally {
                // remove processed type
                currentlyResolving.remove(type);
            }
        }

        return instance;
    }

    /**
     * Returns a trace of the dependencies.
     *
     * @return String builder, so it can be output.
     */
    protected StringBuilder getDependencyTrace() {
        StringBuilder builder = new StringBuilder();
        Class first = currentlyResolving.getFirst();
        Class previous = first;

        if (currentlyResolving.size() > 1) {
            // start at second
            for (int i = 1; i < currentlyResolving.size(); i++) {
                Class current = currentlyResolving.get(i);

                // add trace: previous > current
                builder.append(previous.getName());
                builder.append(" > ");
                builder.append(current.getName());
                builder.append("\n");

                // previous is current
                previous = current;
            }
        }

        // add trace: previous (last) > first
        builder.append(previous.getName());
        builder.append(" > ");
        builder.append(first.getName());

        return builder;
    }

    /**
     * Use an existing mapping to get the instance.
     *
     * @param owner The owner container of the mapping.
     * @param type  The type to get an instance of.
     * @param <T>   Ensures the returned object is cast to the expected type.
     * @return The requested instance.
     * @throws ContainerException when the instance could not be created.
     */
    @SuppressWarnings("unchecked")
    protected <T> T useOwnerInstanceOrMapping(CommandContainer owner, Class<T> type) {
        T instance = (T) owner.instances.get(type);

        // saved instance: return it
        if (instance != null) {
            return instance;
        }

        Object mapping = owner.mappings.get(type);

        // existing mapping is another type: use it to get the instance
        if (mapping instanceof Class) {
            return getInstance((Class<T>) mapping);
        }

        // no existing instance or mapping: create a new instance
        return createInstance(type);
    }

}
