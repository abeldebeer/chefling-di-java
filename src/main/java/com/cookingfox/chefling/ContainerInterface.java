package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface ContainerInterface {

    /**
     * Creates a new instance of `type`, attempting to resolve its full dependency tree. The
     * instance is not stored (that's what {@link #get(Class)} is for), so only use this method
     * directly when you need a NEW instance. It uses the type mappings (from the `map..` methods)
     * to create the instance. If no mapping is available, it attempts to resolve the dependencies
     * by inspecting the constructor parameters. If the created instance implements
     * {@link LifeCycle}, its {@link LifeCycle#onCreate()} method will be called.
     *
     * @param type The type (class, interface) to instantiate.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return Instance of `type`.
     * @throws ContainerException
     */
    <T> T create(Class<T> type) throws ContainerException;

    /**
     * Returns an instance of `type`. If a previously stored instance exists, it will always return
     * that same instance. If there is no stored instance, it will create a new one using
     * {@link #create(Class)}, and store and return that.
     *
     * @param type The type (class, interface) of the object you want to retrieve.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return Instance of `type`.
     * @throws ContainerException
     */
    <T> T get(Class<T> type) throws ContainerException;

    /**
     * Returns whether a stored instance or mapping (from the `map..` methods) exists for `type`.
     *
     * @param type The type (class, interface) to check.
     * @return 'true' if a stored instance or mapping exists, 'false' otherwise.
     */
    boolean has(Class type);

    /**
     * Map `type` to a Factory, which will create an instance of `type` when it is requested (by
     * {@link #create(Class)}). Which specific instance will be created by the Factory is up to the
     * developer. The return value is validated by the Container: if `null` or another unexpected
     * value is returned, an exception will be thrown. If a mapping for `type` already exists when
     * this method is called, an exception will be thrown.
     *
     * @param type    The type (class, interface) of the object that will be created by the Factory.
     * @param factory A factory instance.
     * @param <T>     Ensures a factory of the expected type is passed.
     * @throws ContainerException
     * @see Factory
     */
    <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException;

    /**
     * Map `type` to a specific instance, which will be returned when `type` is requested. This is
     * useful when `type` has dependencies (constructor parameters) that are not resolvable by the
     * Container (e.g. `int`, `boolean`). This instance will be processed by {@link #create(Class)},
     * to make sure the object is properly initialized. If a mapping for `type` already exists when
     * this method is called, an exception will be thrown.
     *
     * @param type     The type (class, interface) you want to map the instance of.
     * @param instance The instance you want to store.
     * @param <T>      Ensures the instance is of the correct type.
     * @throws ContainerException
     */
    <T> void mapInstance(Class<T> type, T instance) throws ContainerException;

    /**
     * Map `type` to a class (`subType`) that extends it. This makes it possible to set a specific
     * implementation of an interface or abstract class. When `type` is requested an instance of
     * `subType` will be created. If a mapping for `type` already exists when this method is called,
     * an exception will be thrown.
     *
     * @param type    The base type (class, interface), which is used when requesting an instance.
     * @param subType The type that extends / implements the base type, which is actually created.
     * @param <T>     Ensures the sub type extends the base type.
     * @throws ContainerException
     */
    <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException;

    /**
     * Removes a stored instance and/or mapping for `type`. If an instance exists and it implements
     * {@link LifeCycle}, its {@link LifeCycle#onDestroy()} method will be called.
     *
     * @param type The type to remove the instance / mapping for.
     * @throws ContainerException
     * @see LifeCycle#onDestroy()
     */
    void remove(Class type) throws ContainerException;

    /**
     * Removes all stored instances and mappings. Use this method to clean up the Container in your
     * application's destroy procedure. For every instance that implements {@link LifeCycle}, its
     * {@link LifeCycle#onDestroy()} method will be called.
     */
    void reset();

}
