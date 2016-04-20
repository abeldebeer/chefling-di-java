package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.event.ContainerEvent;

/**
 * Created by abeldebeer on 16/03/16.
 */
public interface DispatchCommand {

    void dispatch(ContainerEvent event);

}
