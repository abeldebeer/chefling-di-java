package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.events.Listenable;
import com.cookingfox.chefling.api.events.Listener;

import java.util.Arrays;
import java.util.Collection;

/**
 * A {@link Listenable} implementation which returns null listeners from its `getListeners` method.
 */
public class ListenableReturnsNullListeners implements Listenable {

    @Override
    public Collection<Listener> getListeners() {
        return Arrays.asList(new Listener[]{null, null, null});
    }

}
