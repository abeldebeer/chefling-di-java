package com.cookingfox.chefling.api;

/**
 * This interface provides methods that hook into the Container instance creation process:
 * - {@link #initialize()} is called right after an instance of the implementing class is created.
 * This is when the class's dependencies have been resolved and additional initialization operations
 * can be performed.
 * - {@link #dispose()} is called just before an instance of the implementing class is destroyed.
 * This destruction process is initiated when an instance is removed from the Container. It provides
 * a hook for cleaning up instance resources before it is destroyed.
 */
public interface CheflingLifecycle {

    /**
     * Dependencies have been resolved: perform additional initialization operations.
     */
    void initialize();

    /**
     * Instance will be destroyed: perform clean-up operations.
     */
    void dispose();

}
