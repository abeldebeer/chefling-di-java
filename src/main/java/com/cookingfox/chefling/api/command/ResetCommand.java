package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.LifeCycle;

public interface ResetCommand {

    /**
     * Removes all stored instances and mappings. Use this method to clean up the Container in your
     * application's destroy procedure. For every instance that implements {@link LifeCycle}, its
     * {@link LifeCycle#dispose()} method will be called.
     */
    void reset();

}
