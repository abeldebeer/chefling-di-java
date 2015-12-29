package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.events.Listenable;
import com.cookingfox.chefling.api.events.Listener;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * A {@link Listenable} implementation which returns a non-generic listener from its `getListeners`
 * method.
 */
public class ListenableReturnsNonGenericListener implements Listenable {

    public final LinkedList<Object> onEventCalls = new LinkedList<>();

    @Override
    public Collection<Listener> getListeners() {
        final Collection<Listener> listeners = new LinkedHashSet<>();

        listeners.add(new Listener() {
            @Override
            public void onEvent(Object event) {
                onEventCalls.add(event);
            }
        });

        return listeners;
    }

}
