package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.events.Listenable;
import com.cookingfox.chefling.api.events.Listener;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A {@link Listenable} implementation which returns null from its `getListeners` method. It keeps
 * track of the number of calls to `getListeners`.
 */
public class ListenableWithCallLogReturnsNull implements Listenable {

    public final LinkedList<Long> getListenersCalls = new LinkedList<>();

    @Override
    public Collection<Listener> getListeners() {
        getListenersCalls.add(System.nanoTime());

        return null;
    }

}
