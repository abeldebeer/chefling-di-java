package com.cookingfox.fixtures.chefling;

/**
 * A complex circular dependency:
 * - A depends on B
 * - B depends on an interface of C
 * - C depends on A
 */
public class CircularComplex {

    public static class A {
        public B b;

        public A(B b) {
            this.b = b;
        }
    }

    public static class B {
        public CInterface c;

        public B(CInterface c) {
            this.c = c;
        }
    }

    public interface CInterface {
    }

    public static class C implements CInterface {
        public A a;

        public C(A a) {
            this.a = a;
        }
    }

}
