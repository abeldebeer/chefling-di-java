package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where the only resolvable constructor has one parameter.
 */
public class MultipleConstructorsTargetAllowed {

    public NoConstructor first;

    public String second;

    public Object third;

    public MultipleConstructorsTargetAllowed(NoConstructor first, String second, Object third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public MultipleConstructorsTargetAllowed(NoConstructor first, String second) {
        this.first = first;
        this.second = second;
    }

    public MultipleConstructorsTargetAllowed(NoConstructor first) {
        this.first = first;
    }

}
