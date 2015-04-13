package com.cookingfox.chefling.fixtures;

/**
 * A simple circular dependency:
 * - A depends on B
 * - B depends on A
 */
public class CircularSimple {

    public static class A {
        public B b;

        public A(B b) {
            this.b = b;
        }
    }

    public static class B {
        public A a;

        public B(A a) {
            this.a = a;
        }
    }

}
