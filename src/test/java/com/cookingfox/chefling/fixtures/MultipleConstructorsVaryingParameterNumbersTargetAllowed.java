package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where each constructor has a varying number of parameters and
 * the 'target' constructor has two allowed parameters.
 */
public class MultipleConstructorsVaryingParameterNumbersTargetAllowed {

    public NoConstructor first;
    public NoMethodInterface second;
    public String third;
    public Object fourth;

    public MultipleConstructorsVaryingParameterNumbersTargetAllowed(NoConstructor first, NoMethodInterface second) {
        this.first = first;
        this.second = second;
    }

    public MultipleConstructorsVaryingParameterNumbersTargetAllowed(NoConstructor first, NoMethodInterface second, String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public MultipleConstructorsVaryingParameterNumbersTargetAllowed(NoConstructor first, NoMethodInterface second, String third, Object fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

}
