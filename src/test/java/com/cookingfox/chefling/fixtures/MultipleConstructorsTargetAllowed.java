package com.cookingfox.chefling.fixtures;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 13/04/15.
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
