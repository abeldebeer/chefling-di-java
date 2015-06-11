package com.cookingfox.fixtures.chefling;

/**
 * Class with multiple constructors, where each constructor has a varying number of parameters and
 * the only resolvable constructor has no parameters.
 */
public class MultipleConstructorsVaryingParametersTargetEmpty {

    public String first;
    public Object second;

    public MultipleConstructorsVaryingParametersTargetEmpty(String first) {
        this.first = first;
    }

    public MultipleConstructorsVaryingParametersTargetEmpty() {
    }

    public MultipleConstructorsVaryingParametersTargetEmpty(String first, Object second) {
        this.first = first;
        this.second = second;
    }

}
