package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where the only resolvable constructor has one parameter.
 */
public class MultipleConstructorsTargetAllowed {

    public NoConstructor first;

    public NoMethodInterface second;

    public String third;

    public Object fourth;

    public MultipleConstructorsTargetAllowed(NoConstructor first, NoMethodInterface second, String third, Object fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public MultipleConstructorsTargetAllowed(NoConstructor first, NoMethodInterface second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public MultipleConstructorsTargetAllowed(NoConstructor first, NoMethodInterface second) {
        this.first = first;
        this.second = second;
    }

}
