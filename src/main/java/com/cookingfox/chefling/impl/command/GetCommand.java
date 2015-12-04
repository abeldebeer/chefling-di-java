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
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) throws ContainerException {
        assertNonNull(type, "type");

        T instance = (T) _container.instances.get(type);

        if (instance != null) {
            // an instance of this type was previously stored: return it
            return instance;
        }

        Object mapping = _container.mappings.get(type);

        if (mapping instanceof Class) {
            // this type is mapped to another type (through `mapType()`), so use the mapped type to
            // get the instance
            return get((Class<T>) mapping);
        }
        // FIXME: ContainerSet is deprecated
//        else if (mapping == null && children.hasForType(type)) {
//            // a child Container has a mapping / instance for this type: use it
//            return children.getForType(type).get(type);
//        }

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
