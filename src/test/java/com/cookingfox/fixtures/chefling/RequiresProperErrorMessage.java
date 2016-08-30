package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;

/**
 * Contains invalid constructors to check whether the proper error message is created.
 */
public class RequiresProperErrorMessage {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    // unresolvable parameters: from `java.` package
    public RequiresProperErrorMessage(String string, Object object) {
    }

    // unresolvable parameters: from Chefling package
    public RequiresProperErrorMessage(CheflingLifecycle lifecycle, CheflingFactory factory) {
    }

    // dependency on enum
    public RequiresProperErrorMessage(OneValueEnum oneValueEnum) {
    }

    // dependency on member class
    public RequiresProperErrorMessage(MemberClass memberClass) {
    }

    // private and unresolvable
    private RequiresProperErrorMessage(String string, CheflingLifecycle lifecycle) {
    }

    // package-level constructor
    RequiresProperErrorMessage() {
    }

    //----------------------------------------------------------------------------------------------
    // MEMBER CLASSES
    //----------------------------------------------------------------------------------------------

    public class MemberClass {
    }

}
