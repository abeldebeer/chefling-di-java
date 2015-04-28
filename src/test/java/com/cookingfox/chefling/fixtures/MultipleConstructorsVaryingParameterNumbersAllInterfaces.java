package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where each constructor has a different number of parameters and
 * all parameter types are interfaces.
 */
public class MultipleConstructorsVaryingParameterNumbersAllInterfaces {

    private First first;
    private Second second;
    private Third third;

    public MultipleConstructorsVaryingParameterNumbersAllInterfaces(First first, Second second, Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public MultipleConstructorsVaryingParameterNumbersAllInterfaces(First first) {
        this.first = first;
    }

    public MultipleConstructorsVaryingParameterNumbersAllInterfaces(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public interface First {
    }

    public interface Second {
    }

    public interface Third {
    }

}
