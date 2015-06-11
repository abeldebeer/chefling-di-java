package com.cookingfox.fixtures.chefling;

/**
 * A class that has two dependencies, each of which has its own dependency.
 */
public class TwoLevelDependencies {

    public OneParamConstructor first;
    public OneParamConstructor second;

    public TwoLevelDependencies(OneParamConstructor first, OneParamConstructor second) {
        this.first = first;
        this.second = second;
    }
}
