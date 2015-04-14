package com.cookingfox.chefling.fixtures;

/**
 * A circular dependency where the class expects a dependency of itself.
 */
public class CircularSelf {

    public CircularSelf(CircularSelf self) {
    }

}
