package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public interface GetCommand {
    <T> T get(Class<T> type) throws ContainerException;
}
