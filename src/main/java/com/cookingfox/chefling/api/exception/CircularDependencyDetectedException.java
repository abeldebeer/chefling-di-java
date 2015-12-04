package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a circular dependency is detected, e.g. A depends on B, B depends on A.
 */
public class CircularDependencyDetectedException extends ContainerException {

    public CircularDependencyDetectedException(StringBuilder dependencies) {
        super("Circular dependency detected:\n" + dependencies.toString());
    }

}
