package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface ContainerInterface {

    /**
     * Creates a new instance of `type`, attempting to resolve its full dependency tree. The created
     * instance is not stored, so only use this method when you need a new instance.
     *
     * @param type The type to instantiate.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return New instance of the type.
     * @throws ContainerException
     */
    <T> T create(Class<T> type) throws ContainerException;

    /**
     * If there is no stored instance of the provided type, a new one is created using
     * {@link #create(Class)}. If the type is mapped to a sub type using {@link #map(Class, Class)},
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
     * Returns whether a stored instance is available for the provided type. Does not take into
     * account the type mappings from {@link #map(Class, Class)}.
     *
     * @param type The type you want to check.
     * @return 'true' if a stored value is available, 'false' otherwise.
     */
    boolean has(Class type);

    /**
     * Instructs the container to return an instance of `subType` when `type` is requested. This
     * makes it possible to set a specific implementation of an interface or abstract class.
     *
     * @param type    The base type, which is used when requesting an instance.
     * @param subType The type that extends / implements the base type, which is actually created.
     * @param <T>     Ensures the sub type extends the base type.
     * @throws ContainerException
     */
    <T> void map(Class<T> type, Class<? extends T> subType) throws ContainerException;

    /**
     * Not all types can be resolved by the container (e.g. primitive types like `boolean`), so this
     * method can be used to store a specific instance of a type. If a previously stored instance
     * is available an exception will be thrown, because it is not allowed to be replaced.
     *
     * @param type     The type you want to map the instance of.
     * @param instance The instance you want to store.
     * @param <T>      Ensures the instance is of the correct type.
     * @throws ContainerException
     */
    <T> void set(Class<T> type, T instance) throws ContainerException;

    /**
     * Not all types can be resolved by the container (e.g. primitive types like `boolean`), so this
     * method can be used to store a specific instance of a type. The `replace` parameter determines
     * whether a previously stored instance for this type will be replaced. Use this method with
     * caution, because it can lead to bugs that are hard to trace!
     *
     * @param type     The type you want to map the instance of.
     * @param instance The instance you want to store.
     * @param replace  If 'true': replace a previously stored instance for this type. If false:
     *                 throw an exception (default).
     * @param <T>      Ensures the instance is of the correct type.
     * @throws ContainerException
     */
    <T> void set(Class<T> type, T instance, boolean replace) throws ContainerException;

}
