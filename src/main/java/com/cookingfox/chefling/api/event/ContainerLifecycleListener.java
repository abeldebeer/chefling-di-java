package com.cookingfox.chefling.api.event;

/**
 * Created by abeldebeer on 21/03/16.
 */
public interface ContainerLifecycleListener extends ContainerListener {

    void onContainerEvent(PostConstructEvent event);

    void onContainerEvent(PreDestroyEvent event);

}
