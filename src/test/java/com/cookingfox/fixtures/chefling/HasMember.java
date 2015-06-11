package com.cookingfox.fixtures.chefling;

/**
 * Wrapper for a non-static member class.
 */
public class HasMember {

    public class MemberClass {
    }

    public static Class getMemberClass() {
        return MemberClass.class;
    }

}
