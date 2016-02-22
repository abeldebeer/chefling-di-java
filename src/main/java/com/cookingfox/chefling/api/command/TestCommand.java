package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface TestCommand {

    /**
     * Attempts to recursively resolve all mappings of the current Container, its parents and its
     * children. This operation uses {@link Container#get(Class)}. This is useful during development
     * to validate the Container configuration. Do NOT use this method in production, since it
     * overrides the "lazy loading" ability of the container!
     *
     * @throws ContainerException when an error occurs.
     * @see Container#get(Class)
     */
    void test();

}
