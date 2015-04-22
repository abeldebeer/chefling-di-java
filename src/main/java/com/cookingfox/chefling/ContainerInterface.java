package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface ContainerInterface {

    /**
     * Creates a new instance of `type`, attempting to resolve its full dependency tree. The created
     * instance is not stored, so only use this method when you need a new instance. It uses the
     * type mappings that are done using {@link #mapType(Class, Class)} to create an instance of the
     * expected type.
     *
     * @param type The type to instantiate.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return New instance of the type.
     * @throws ContainerException
     */
    <T> T create(Class<T> type) throws ContainerException;

    /**
     * If there is no stored instance of the provided type, a new one is created using
     * {@link #create(Class)}. If the type is mapped to a sub type using {@link #mapType(Class, Class)},
     * it uses the sub type to create the instance. Once created, the instance is stored and
     * returned.
     *
     * @param type The type of the object you want to retrieve.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return Instance of type.
     * @throws ContainerException
     */
    <T> T get(Class<T> type) throws ContainerException;

    /**
     * Returns whether a stored instance or mapping (from {@link #mapType(Class, Class)}) exists for
     * `type`.
     *
     * @param type The type you want to check.
     * @return 'true' if a stored instance or mapping exists, 'false' otherwise.
     */
    boolean has(Class type);

    /**
     * Map a type to a Factory, so that it will create an instance of `type` when it is requested.
     *
     * @param type    The type of the object that will be created by the factory.
     * @param factory A factory instance.
     * @param <T>     Ensures a factory of the expected type is passed.
     * @throws ContainerException
     */
    <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException;

    /**
     * Not all types can be resolved by the container (e.g. primitive types like `boolean`), so this
     * method can be used to store a specific instance of a type. An exception will be thrown if a
     * previously stored instance or mapping (using {@link #mapType(Class, Class)}) for this type
     * already exists.
     *
     * @param type     The type you want to map the instance of.
     * @param instance The instance you want to store.
     * @param <T>      Ensures the instance is of the correct type.
     * @throws ContainerException
     */
    <T> void mapInstance(Class<T> type, T instance) throws ContainerException;

    /**
     * Instructs the container to return an instance of `subType` when `type` is requested. This
     * makes it possible to set a specific implementation of an interface or abstract class. When
     * an instance of `type` has already been stored an exception will be thrown, because it cannot
     * override this value.
     *
     * @param type    The base type, which is used when requesting an instance.
     * @param subType The type that extends / implements the base type, which is actually created.
     * @param <T>     Ensures the sub type extends the base type.
     * @throws ContainerException
     */
    <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException;

    /**
     * Removes a stored instance and/or mapping for the provided type. If the instance is an
     * instance of {@link LifeCycle}, it will call its onDestroy method.
     *
     * @param type The type to remove the instance / mapping for.
     * @see LifeCycle#onDestroy()
     */
    void remove(Class type);

    /**
     * Removes all stored instances and mappings.
     */
    void reset();

}
