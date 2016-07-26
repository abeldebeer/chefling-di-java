package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.CheflingLifecycle;

public interface DisposeContainerCommand {

    /**
     * Removes all stored instances and mappings. Use this method to clean up the container in your
     * application's destroy procedure. For every instance that implements
     * {@link CheflingLifecycle}, its {@link CheflingLifecycle#dispose()} method will be called.
     */
    void disposeContainer();

}
