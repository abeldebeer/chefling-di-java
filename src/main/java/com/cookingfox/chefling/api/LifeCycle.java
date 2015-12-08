package com.cookingfox.chefling.api;

/**
 * By implementing this interface, the Container will call its methods when an instance of the type
 * is created and destroyed.
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
