package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public class CreateCommand extends AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public CreateCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        super(container, instances, mappings);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) throws ContainerException {
        Object mapping = mappings.get(type);
        Class typeToCreate = type;

        if (mapping instanceof Factory) {
            return resolveUsingFactory((Factory<T>) mapping, type);
        } else if (mapping instanceof Class) {
            typeToCreate = (Class) mapping;
        }

        Constructor constructor = getDefaultConstructor(typeToCreate);
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
            throw new TypeInstantiationException(typeToCreate, e);
        }
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

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
                        "that are not resolvable by the container");
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

        // check if all parameters are resolvable by container
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
