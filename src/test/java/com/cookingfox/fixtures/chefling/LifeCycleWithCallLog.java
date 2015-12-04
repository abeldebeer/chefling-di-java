package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.LifeCycle;

import java.util.LinkedList;

/**
 * Example {@link LifeCycle} implementation, that logs the calls to its methods.
 */
public class LifeCycleWithCallLog implements LifeCycle {

    public final LinkedList<Long> onCreateCalls = new LinkedList<Long>();
    public final LinkedList<Long> onDestroyCalls = new LinkedList<Long>();

    @Override
    public void onCreate() {
        onCreateCalls.push(System.nanoTime());
    }

    @Override
    public void onDestroy() {
        onDestroyCalls.push(System.nanoTime());
    }

}
