package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface ContainerInterface {

    /**
     * Creates and returns a new instance of the provided type, attempting to resolve its full
     * dependency tree.
     *
     * @param type   The type to instantiate.
     * @param <Type> Ensures the returned object is cast to the expected type.
     * @return New instance of the type.
     * @throws ContainerException
     */
    <Type> Type create(Class<Type> type) throws ContainerException;

    /**
     * Returns an instance of the provided type. If no stored instance exists, it attempts to create
     * a new one (using {@link #create(Class)}). For creation, it takes into account the type
     * mappings (from {@link #map(Class, Class)}) to ensure an instance of the correct class is
     * created. It returns the same instance on subsequent calls.
     *
     * @param type   The type of the object you want to retrieve.
     * @param <Type> Ensures the returned object is cast to the expected type.
     * @return Instance of type.
     * @throws ContainerException
     */
    <Type> Type get(Class<Type> type) throws ContainerException;

    /**
     * Returns whether a stored instance is available for the provided type. Does not take into
     * account the type mappings from {@link #map(Class, Class)}.
     *
     * @param type The type you want to check.
     * @return 'true' if a stored value is available, 'false' otherwise.
     */
    boolean has(Class type);

    /**
     * Maps a type to a sub type (e.g. an interface to a class implementing it), so that it can be
     * created in {@link #get(Class)}.
     *
     * @param type    The base type, which is used when requesting an instance.
     * @param subType The type that extends / implements the base type, which actually created.
     * @param <Type>  Ensures the sub type extends the base type.
     * @throws ContainerException
     */
    <Type> void map(Class<Type> type, Class<? extends Type> subType) throws ContainerException;

    /**
     * Stores an instance of the provided type. If a stored value of this type is already available,
     * it will be overwritten. This method also allows you to map an instance to a super type, for
     * example an interface.
     *
     * @param type     The type you want to map the instance of.
     * @param instance The instance you want to store.
     * @param <Type>   Ensures the instance is of the correct type.
     * @throws ContainerException
     */
    <Type> void set(Class<Type> type, Type instance) throws ContainerException;

}
