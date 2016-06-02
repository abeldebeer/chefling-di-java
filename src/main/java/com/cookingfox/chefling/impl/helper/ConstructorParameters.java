package com.cookingfox.chefling.impl.helper;

import java.lang.reflect.Constructor;

/**
 * Wraps a type's selected constructor + parameter types, so it can be cached.
 */
public final class ConstructorParameters {

    public final Constructor constructor;
    public final Class[] parameterTypes;

    public ConstructorParameters(Constructor constructor, Class[] parameterTypes) {
        this.constructor = constructor;
        this.parameterTypes = parameterTypes;
    }

}
