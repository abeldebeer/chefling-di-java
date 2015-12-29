package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.exception.ContainerException;

public interface DispatchCommand {

    <T> void dispatch(T event) throws ContainerException;

}
