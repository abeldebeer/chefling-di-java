package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.exception.ContainerException;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public interface Factory<T> {
    T create(Container container) throws ContainerException;
}
