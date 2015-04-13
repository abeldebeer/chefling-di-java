package com.cookingfox.chefling.fixtures;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 13/04/15.
 */
public class MultipleConstructorsTargetEmpty {

    public String first;

    public Object second;

    public MultipleConstructorsTargetEmpty(String first, Object second) {
        this.first = first;
        this.second = second;
    }

    public MultipleConstructorsTargetEmpty(String first) {
        this.first = first;
    }

    public MultipleConstructorsTargetEmpty() {
    }

}
