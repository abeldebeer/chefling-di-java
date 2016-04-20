package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.event.ContainerListener;

/**
 * Created by abeldebeer on 16/03/16.
 */
public class BasicContainerEventListener implements ContainerListener {

    public BasicContainerEvent receivedEvent;

    public void onContainerEvent(BasicContainerEvent event) {
        receivedEvent = event;
    }

}
