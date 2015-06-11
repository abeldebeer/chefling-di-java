package com.cookingfox.fixtures.chefling;

/**
 * Non-public classes.
 */
public class NonPublicClasses {

    private static class PrivateClass {
    }

    protected static class ProtectedClass {
    }

    public static Class getPrivateClass() {
        return PrivateClass.class;
    }

    public static Class getProtectedClass() {
        return ProtectedClass.class;
    }

}
