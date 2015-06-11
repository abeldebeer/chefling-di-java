package com.cookingfox.fixtures.chefling;

/**
 * Class with multiple constructors, where each constructor has a different number of parameters and
 * all parameter types are interfaces.
 */
public class MultipleConstructorsVaryingParametersAllInterfaces {

    private First first;
    private Second second;
    private Third third;

    public MultipleConstructorsVaryingParametersAllInterfaces(First first, Second second, Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public MultipleConstructorsVaryingParametersAllInterfaces(First first) {
        this.first = first;
    }

    public MultipleConstructorsVaryingParametersAllInterfaces(First first, Second second) {
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
