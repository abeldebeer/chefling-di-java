package com.cookingfox.chefling.fixtures;

import com.cookingfox.chefling.LifeCycle;

import java.util.LinkedList;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 21/04/15.
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
