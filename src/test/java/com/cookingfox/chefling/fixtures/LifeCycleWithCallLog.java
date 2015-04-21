package com.cookingfox.chefling.fixtures;

import com.cookingfox.chefling.LifeCycle;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 21/04/15.
 */
public class LifeCycleWithCallLog implements LifeCycle {

    public boolean createCalled = false;
    public boolean destroyCalled = false;

    @Override
    public void create() {
        createCalled = true;
    }

    @Override
    public void destroy() {
        destroyCalled = true;
    }

}
