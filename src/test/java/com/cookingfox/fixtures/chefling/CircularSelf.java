package com.cookingfox.fixtures.chefling;

/**
 * A circular dependency where the class expects a dependency of itself.
 */
public class CircularSelf {

    public CircularSelf(CircularSelf self) {
    }

}
