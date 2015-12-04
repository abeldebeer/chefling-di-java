package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public interface AddChildCommand {
    void addChild(Container container) throws ContainerException;
}
