package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where each constructor has a varying number of parameters and
 * the only resolvable constructor has no parameters.
 */
public class MultipleConstructorsVaryingParameterNumbersTargetEmpty {

    public String first;
    public Object second;

    public MultipleConstructorsVaryingParameterNumbersTargetEmpty() {
    }

    public MultipleConstructorsVaryingParameterNumbersTargetEmpty(String first) {
        this.first = first;
    }

    public MultipleConstructorsVaryingParameterNumbersTargetEmpty(String first, Object second) {
        this.first = first;
        this.second = second;
    }

}
