package com.cookingfox.chefling.fixtures;

/**
 * Class with multiple constructors, where each has the same number of parameters and only one is
 * resolvable.
 */
public class MultipleConstructorsOneParameterTargetAllowed {

    public MultipleConstructorsOneParameterTargetAllowed(String exampleString) {
    }

    public MultipleConstructorsOneParameterTargetAllowed(Exception exampleException) {
    }

    public MultipleConstructorsOneParameterTargetAllowed(NoConstructor exampleClass) {
    }

    public MultipleConstructorsOneParameterTargetAllowed(Object exampleObject) {
    }

    public MultipleConstructorsOneParameterTargetAllowed(Boolean exampleBoolean) {
    }

}
