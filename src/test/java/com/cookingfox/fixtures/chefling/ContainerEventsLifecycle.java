package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingLifecycle;

import java.util.Collection;

/**
 * Implementation of {@link CheflingLifecycle} which logs lifecycle events.
 */
public class ContainerEventsLifecycle implements CheflingLifecycle {

    final Collection<ContainerEventsEnum> events;

    public ContainerEventsLifecycle(Collection<ContainerEventsEnum> events) {
        this.events = events;
    }

    @Override
    public void initialize() {
        events.add(ContainerEventsEnum.INITIALIZE);
    }

    @Override
    public void dispose() {
        events.add(ContainerEventsEnum.DISPOSE);
    }

}
