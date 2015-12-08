package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.LifeCycle;

import java.util.LinkedList;

/**
 * Example {@link LifeCycle} implementation, that logs the calls to its methods.
 */
public class LifeCycleWithCallLog implements LifeCycle {

    public final LinkedList<Long> initializeCalls = new LinkedList<>();
    public final LinkedList<Long> disposeCalls = new LinkedList<>();

    @Override
    public void initialize() {
        initializeCalls.push(System.nanoTime());
    }

    @Override
    public void dispose() {
        disposeCalls.push(System.nanoTime());
    }

}
