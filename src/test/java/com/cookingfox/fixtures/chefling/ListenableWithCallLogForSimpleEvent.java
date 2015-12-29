package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.events.Listenable;
import com.cookingfox.chefling.api.events.Listener;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * A {@link Listenable} implementation which returns one listener from its `getListeners` method. It
 * keeps a reference to the event object that was passed.
 */
public class ListenableWithCallLogForSimpleEvent implements Listenable {

    public final LinkedList<Object> onEventCalls = new LinkedList<>();

    @Override
    public Collection<Listener> getListeners() {
        final Collection<Listener> listeners = new LinkedHashSet<>();

        listeners.add(new Listener<SimpleEvent>() {
            @Override
            public void onEvent(SimpleEvent event) {
                onEventCalls.add(event);
            }
        });

        return listeners;
    }

}
