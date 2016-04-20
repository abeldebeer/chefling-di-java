package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.GetCommand;
import com.cookingfox.chefling.api.event.ContainerListener;
import com.cookingfox.chefling.api.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.impl.helper.RegisteredListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @see GetCommand
 */
class GetCommandImpl extends AbstractCommand implements GetCommand {

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

    public GetCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> T get(Class<T> type) {
        assertNonNull(type, "type");

        CommandContainer existingOwner = findOne(_container, HasMappingMatcher.get(type));

        if (existingOwner == null) {
            return createInstance(type);
        }

        return useMapping(existingOwner, type);
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
                instance = _container.create(type);
                _container.instances.put(type, instance);
            } finally {
                // remove processed type
                currentlyResolving.remove(type);
            }
        }

        if (instance instanceof ContainerListener) {
            registerListener(type, (ContainerListener) instance);
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

    /**
     * Validates the subscriber method and returns its event type.
     * FIXME: 16/03/16 Add tests for this method
     */
    protected Class getValidEventType(final Method method) {
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new ContainerException("Event handler methods must be public");
        }

        final Class[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length != 1) {
            throw new ContainerException("Event handler can only have one parameter: the event object");
        }

        final Class eventClass = parameterTypes[0];
        final Package eventPackage = eventClass.getPackage();

        if (eventClass.isInterface()) {
            throw new ContainerException("Interface not allowed!");
        }

        if (eventPackage != null && eventPackage.getName().startsWith("java.")) {
            throw new ContainerException("Event types from `java.*` package are not allowed");
        }

        return eventClass;
    }

    protected void registerListener(final Class type, final ContainerListener instance) {
        final Method[] subjectMethods = type.getDeclaredMethods();
        final Set<RegisteredListener> listeners = new LinkedHashSet<>();

        // extract all subscriber's event listeners
        for (Method method : subjectMethods) {
            // no subscriber: skip
            if (!ContainerListener.METHOD_NAME.equals(method.getName())) {
                continue;
            }

            final Class eventClass = getValidEventType(method);

            listeners.add(new RegisteredListener(instance, method, eventClass));
        }

        if (listeners.isEmpty()) {
            throw new ContainerException("ContainerListener does not have any methods named '" +
                    ContainerListener.METHOD_NAME + "'");
        }

        for (final RegisteredListener listener : listeners) {
            final Class eventClass = listener.eventClass;

            Set<RegisteredListener> _listeners = _container.listeners.get(eventClass);

            if (_listeners == null) {
                _listeners = new LinkedHashSet<>();
                _container.listeners.put(eventClass, _listeners);
            }

            _listeners.add(listener);
        }
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
    protected <T> T useMapping(CommandContainer owner, Class<T> type) {
        Object mapping = findMapping(owner, type);

        if (type.isInstance(mapping)) {
            // existing mapping is instance: save on mapping owner
            owner.instances.put(type, mapping);

            return (T) mapping;
        } else if (mapping instanceof Class) {
            // existing mapping is another type: use it to get the instance
            return get((Class<T>) mapping);
        }

        // create the instance
        return createInstance(type);
    }

}
