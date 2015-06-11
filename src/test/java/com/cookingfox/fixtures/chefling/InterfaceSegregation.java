package com.cookingfox.fixtures.chefling;

/**
 * Example where a class implements an interface, which in turn extends two other interfaces.
 */
public class InterfaceSegregation {

    public interface Talkable {
        void talk();
    }

    public interface Walkable {
        void walk();
    }

    public interface Person extends Talkable, Walkable {
    }

    public static class JohnDoe implements Person {
        @Override
        public void talk() {
            // noop
        }

        @Override
        public void walk() {
            // noop
        }
    }

}
