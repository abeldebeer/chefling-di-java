package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;

/**
 * Contains invalid constructors to check whether the proper error message is created.
 */
public class RequiresProperErrorMessage {

    // package-level constructor
    RequiresProperErrorMessage() {
    }

    // unresolvable parameters: from `java.` package
    public RequiresProperErrorMessage(String string, Object object) {
    }

    // unresolvable parameters: from Chefling package
    public RequiresProperErrorMessage(CheflingLifecycle lifecycle, CheflingFactory factory) {
    }

}
