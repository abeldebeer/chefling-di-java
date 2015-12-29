package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.DispatchCommand;
import com.cookingfox.chefling.api.events.Listenable;
import com.cookingfox.chefling.api.events.Listener;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @see DispatchCommand
 */
class DispatchCommandImpl extends AbstractCommand implements DispatchCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public DispatchCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public <T> void dispatch(T event) throws ContainerException {
        assertNonNull(event, "event");

        // find listenables in instances
        for (Object instance : _container.instances.values()) {
            if (instance instanceof Listenable) {
                callListeners((Listenable) instance, event);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    protected void callListeners(Listenable listenable, Object event) {
        Collection<Listener> listeners = listenable.getListeners();

        // `getListeners` returned empty list
        if (listeners == null || listeners.size() == 0) {
            return;
        }

        for (Listener listener : listeners) {
            Class genericType = getGenericType(listener);

            // generic is valid
            if (genericType != null && genericType.isInstance(event)) {
                listener.onEvent(event);
            }
        }
    }

    protected Class getGenericType(Listener listener) {
        if (listener == null) {
            return null;
        }

        // find parameterized type
        for (Type type : listener.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterized = (ParameterizedType) type;

                return (Class) parameterized.getActualTypeArguments()[0];
            }
        }

        return null;
    }

}
