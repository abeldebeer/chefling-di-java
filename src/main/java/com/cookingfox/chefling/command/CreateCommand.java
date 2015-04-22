package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.LifeCycle;
import com.cookingfox.chefling.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Implementation of {@link ContainerInterface#create(Class)}.
 */
public class CreateCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * @see AbstractCommand#AbstractCommand(ContainerInterface, Map, Map)
     */
    public CreateCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see ContainerInterface#create(Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) throws ContainerException {
        Object mapping = mappings.get(type);
        T instance;

        if (mapping instanceof Factory) {
            // use factory to create instance
            instance = resolveUsingFactory((Factory<T>) mapping, type);
        } else if (type.isInstance(mapping)) {
            // mapping is instance
            instance = (T) mapping;
        } else if (mapping instanceof Class) {
            // create instance using mapped type
            instance = createInstance((Class<T>) mapping);
        } else {
            // no mapping: create instance using provided type
            instance = createInstance(type);
        }

        // call life cycle onCreate
        if (instance instanceof LifeCycle) {
            ((LifeCycle) instance).onCreate();
        }

        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of `type`, attempting to resolve its full dependency tree.
     *
     * @param type The type to instantiate.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return New instance of the type.
     * @throws ContainerException
     */
    @SuppressWarnings("unchecked")
    protected <T> T createInstance(Class<T> type) throws ContainerException {
        Constructor constructor = getDefaultConstructor(type);
        Class[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        // gather constructor parameters based on their types
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = container.get(parameterTypes[i]);
        }

        try {
            // create a new instance, passing the constructor parameters
            return (T) constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new TypeInstantiationException(type, e);
        }
    }

    /**
     * Selects the most reasonable default constructor, based on its modifiers and parameters.
     * Throws if the type is not instantiable.
     *
     * @param type Type to get the constructor of.
     * @return First constructor.
     * @throws TypeNotAllowedException
     */
    protected Constructor getDefaultConstructor(Class type) throws TypeNotAllowedException {
        isInstantiable(type);

        Constructor[] constructors = type.getDeclaredConstructors();
        Constructor selectedConstructor = null;
        boolean nonPublicConstructor = false;

        for (Constructor current : constructors) {
            // constructor not public? skip
            if (!Modifier.isPublic(current.getModifiers())) {
                nonPublicConstructor = true;
                continue;
            } else {
                nonPublicConstructor = false;
            }

            selectedConstructor = selectConstructorBasedOnParameters(current);

            if (selectedConstructor != null) {
                break;
            }
        }

        if (selectedConstructor == null) {
            if (nonPublicConstructor) {
                throw new TypeNotInstantiableException(type, "it has no public constructor");
            } else {
                throw new TypeNotInstantiableException(type, "its constructors have parameters " +
                        "that are not resolvable by the Container");
            }
        }

        return selectedConstructor;
    }

    /**
     * Resolves a type using a Factory instance. Throws if the returned value is null or invalid.
     *
     * @param factory The Factory object.
     * @param type    The expected type that the Factory should return.
     * @param <T>     Ensures the return value is cast to expected type.
     * @return The created instance.
     * @throws ContainerException
     */
    protected <T> T resolveUsingFactory(Factory<T> factory, Class<T> type) throws ContainerException {
        T instance = factory.create(container);

        if (instance == null) {
            throw new FactoryReturnedNullException(type);
        } else if (!type.isInstance(instance)) {
            throw new FactoryReturnedUnexpectedValueException(type, instance);
        }

        return instance;
    }

    /**
     * Check whether the constructor parameters are resolvable.
     *
     * @param constructor The constructor to check.
     * @return The constructor if valid, else null.
     */
    protected Constructor selectConstructorBasedOnParameters(Constructor constructor) {
        Class[] parameterTypes = constructor.getParameterTypes();

        // no parameters? select this one
        if (parameterTypes.length == 0) {
            return constructor;
        }

        boolean allParametersResolvable = false;

        // check if all parameters are resolvable by Container
        for (Class parameterType : parameterTypes) {
            // has type instance / mapping: ok!
            if (container.has(parameterType)) {
                allParametersResolvable = true;
                continue;
            }

            try {
                // type is allowed?
                isInstantiable(parameterType);
                allParametersResolvable = true;
                continue;
            } catch (TypeNotAllowedException e) {
                // type not allowed / instantiable
            }

            allParametersResolvable = false;
            break;
        }

        return allParametersResolvable ? constructor : null;
    }

}
