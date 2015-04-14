package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where the only resolvable constructor has no parameters.
 */
public class MultipleConstructorsTargetEmpty {

    public String first;

    public Object second;

    public MultipleConstructorsTargetEmpty(String first, Object second) {
        this.first = first;
        this.second = second;
    }

    public MultipleConstructorsTargetEmpty(String first) {
        this.first = first;
    }

    public MultipleConstructorsTargetEmpty() {
    }

}
