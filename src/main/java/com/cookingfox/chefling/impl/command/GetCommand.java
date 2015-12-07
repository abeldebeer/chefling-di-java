package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.util.LinkedList;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class GetCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.GetCommand {
    protected final LinkedList<Class> currentlyResolving = new LinkedList<Class>();

    public GetCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        assertNonNull(type, "type");

        if (_container.has(type)) {
            return getExisting(type);
        }

        return createNew(type);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    protected <T> T createNew(Class<T> type) throws ContainerException {
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
                instance = _container.create(type);
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

    @SuppressWarnings("unchecked")
    protected <T> T getExisting(final Class<T> type) throws ContainerException {
        Object existing = findMapping(_container, type);

        if (existing instanceof Class) {
            return get((Class<T>) existing);
        } else if (type.isInstance(existing)) {
            return (T) existing;
        }

        return createNew(type);
    }

}
