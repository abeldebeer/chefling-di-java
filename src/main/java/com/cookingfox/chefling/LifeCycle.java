package com.cookingfox.chefling;

/**
 * Implement this interface to have the container call the create() and destroy() methods in your
 * class.
 */
public interface LifeCycle {

    /**
     * Initialize the object.
     */
    void create();

    /**
     * Clean up the object.
     */
    void destroy();

}
