package com.cookingfox.chefling;

/**
 * Implement this interface to have the container call the onCreate() and onDestroy() methods in
 * your class.
 */
public interface LifeCycle {

    /**
     * Initialize the object.
     */
    void onCreate();

    /**
     * Clean up the object.
     */
    void onDestroy();

}
