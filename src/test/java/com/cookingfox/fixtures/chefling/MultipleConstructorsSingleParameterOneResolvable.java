package com.cookingfox.fixtures.chefling;

/**
 * Class with multiple constructors, where each has the same number of parameters and only one is
 * resolvable.
 */
public class MultipleConstructorsSingleParameterOneResolvable {

    public MultipleConstructorsSingleParameterOneResolvable(String exampleString) {
    }

    public MultipleConstructorsSingleParameterOneResolvable(Exception exampleException) {
    }

    public MultipleConstructorsSingleParameterOneResolvable(NoConstructor exampleClass) {
    }

    public MultipleConstructorsSingleParameterOneResolvable(Object exampleObject) {
    }

    public MultipleConstructorsSingleParameterOneResolvable(Boolean exampleBoolean) {
    }

}
