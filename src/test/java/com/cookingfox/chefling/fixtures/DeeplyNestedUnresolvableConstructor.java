package com.cookingfox.chefling.fixtures;

/**
 * Example structure where a deeply nested class has an unresolvable constructor.
 */
public class DeeplyNestedUnresolvableConstructor {

    public static class A {
        public A(B b) {
        }
    }

    public static class B {
        public B(C c) {
        }
    }

    public static class C {
        public C(D d) {
        }
    }

    public static class D {
        public D(E e) {
        }
    }

    public static class E {
        public E(String value) { // <-- unresolvable
        }
    }

}
