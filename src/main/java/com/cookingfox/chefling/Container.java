package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @see com.cookingfox.chefling.ContainerInterface
 */
public class Container implements ContainerInterface {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Stored instances, where key is the type and value is the instance.
     */
    protected final Map<Class, Object> instances = new HashMap<Class, Object>();

    /**
     * Temporary 'log' of the types that are in the process of being resolved. After the type is
     * successfully resolved, the entry is removed. This is used to detect circular dependencies.
     */
    protected final List<Class> currentlyResolving = new LinkedList<Class>();

    /**
     * Type map, where key is the type that is requested and value is either a sub type that needs
     * to be created (through {@link #map(Class, Class)}) or a Factory.
     */
    protected final Map<Class, Object> mappings = new HashMap<Class, Object>();

    /**
     * Convenience singleton for apps using a process-wide Container instance.
     */
    protected static volatile Container defaultInstance;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Default constructor: initializes the container.
     */
    public Container() {
        initialize();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see com.cookingfox.chefling.ContainerInterface#create(Class)
     */
    @Override
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
            parameters[i] = get(parameterTypes[i]);
        }

        try {
            // create a new instance, passing the constructor parameters
            return (T) constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new TypeInstantiationException(typeToCreate, e);
        }
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#factory(Class, Factory)
     */
    @Override
    public <T> void factory(Class<T> type, Factory<T> factory) throws ContainerException {
        isAllowed(type);

        if (has(type)) {
            throw new TypeMappingAlreadyExistsException(type);
        }

        mappings.put(type, factory);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#get(Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) throws ContainerException {
        T instance = (T) instances.get(type);

        if (instance != null) {
            return instance;
        }

        synchronized (currentlyResolving) {
            // if the requested type is already being processed, it indicates a circular dependency
            if (currentlyResolving.contains(type)) {
                throw new CircularDependencyDetectedException(getDependencyTrace());
            } else {
                // store currently processed type
                currentlyResolving.add(type);
            }

            try {
                // create and store instance
                instance = create(type);
                instances.put(type, instance);
            } finally {
                // remove processed type
                currentlyResolving.remove(type);
            }
        }

        return instance;
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#has(Class)
     */
    @Override
    public boolean has(Class type) {
        return instances.containsKey(type) || mappings.containsKey(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#map(Class, Class)
     */
    @Override
    public <T> void map(Class<T> type, Class<? extends T> subType) throws ContainerException {
        // validate the sub type extends the type
        if (subType.equals(type) || !type.isAssignableFrom(subType)) {
            throw new NotASubTypeException(type, subType);
        }

        // validate the types
        isAllowed(type);
        isInstantiable(subType);

        synchronized (this) {
            // check whether a mapping or instance already exists
            if (has(type)) {
                throw new TypeMappingAlreadyExistsException(type);
            }

            mappings.put(type, subType);
        }
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#remove(Class)
     */
    @Override
    public void remove(Class type) {
        instances.remove(type);
        mappings.remove(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#set(Class, Object)
     */
    @Override
    public <T> void set(Class<T> type, T instance) throws ContainerException {
        set(type, instance, false);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#set(Class, Object, boolean)
     */
    @Override
    public <T> void set(Class<T> type, T instance, boolean replace) throws ContainerException {
        // validate the instance is an instance of type
        if (!type.isInstance(instance)) {
            throw new NotAnInstanceOfTypeException(type, instance);
        }

        // if a mapping for type exists, throw
        if (mappings.containsKey(type)) {
            throw new TypeMappingAlreadyExistsException(type);
        }

        isAllowed(type);

        // if an instance of type is already stored, throw
        if (!replace && instances.containsKey(type)) {
            throw new ReplaceInstanceNotAllowedException(type, instances.get(type), instance);
        }

        instances.put(type, instance);
    }

    /**
     * Convenience singleton for apps using a process-wide Container instance.
     *
     * @return Default Container instance.
     */
    public static Container getDefault() {
        if (defaultInstance == null) {
            synchronized (Container.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Container();
                }
            }
        }

        return defaultInstance;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Initializes the container.
     */
    protected void initialize() {
        // map this instance to its class and interface
        instances.put(Container.class, this);
        instances.put(ContainerInterface.class, this);
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
        T instance = factory.create(this);

        if (instance == null) {
            throw new FactoryReturnedNullException(type);
        } else if (!type.isInstance(instance)) {
            throw new FactoryReturnedUnexpectedValueException(type, instance);
        }

        return instance;
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
                        "that are not resolvable by the container");
            }
        }

        return selectedConstructor;
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
            if (has(parameterType)) {
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

    /**
     * Is this type allowed to be mapped in the container?
     *
     * @param type The type to validate.
     * @throws TypeNotAllowedException
     */
    protected void isAllowed(Class type) throws TypeNotAllowedException {
        String errorReason = null;
        int modifiers = type.getModifiers();

        if (isPrimitiveOrWrapper(type)) {
            errorReason = "primitive";
        } else if (isLanguageConstruct(type)) {
            errorReason = "a Java language construct";
        } else if (ContainerInterface.class.isAssignableFrom(type)) {
            errorReason = "the Container instance that should not be overridden";
        } else if (Factory.class.isAssignableFrom(type)) {
            errorReason = "an implementation of the Factory interface, which has a specific use in the Container";
        } else if (Throwable.class.isAssignableFrom(type)) {
            errorReason = "an exception (extends Throwable)";
        } else if (type.isEnum()) {
            errorReason = "an enum";
        } else if (type.isAnnotation()) {
            errorReason = "an annotation";
        } else if (!Modifier.isPublic(modifiers)) {
            errorReason = "not public";
        } else if (type.isMemberClass() && !Modifier.isStatic(modifiers)) {
            errorReason = "a member class";
        }

        if (errorReason != null) {
            throw new TypeNotAllowedException(type, errorReason);
        }
    }

    /**
     * Can this type be instantiated?
     *
     * @param type The type to validate.
     * @throws TypeNotAllowedException
     */
    protected void isInstantiable(Class type) throws TypeNotAllowedException {
        isAllowed(type);

        String errorReason = null;

        if (type.isInterface()) {
            errorReason = "an interface";
        } else if (Modifier.isAbstract(type.getModifiers())) {
            errorReason = "an abstract class";
        }

        if (errorReason != null) {
            throw new TypeNotInstantiableException(type, "it is ".concat(errorReason));
        }
    }

    /**
     * Is the type a primitive (e.g. int) or primitive wrapper class (e.g. java.lang.Integer)?
     *
     * @param type The type to validate.
     * @return Whether the type is primitive.
     */
    protected boolean isPrimitiveOrWrapper(Class type) {
        if (type.isPrimitive()) {
            return true;
        }

        Class[] wrapperClasses = {Boolean.class, Byte.class, Character.class, Double.class,
                Float.class, Integer.class, Long.class, Short.class, Void.class};

        for (Class wrapperClass : wrapperClasses) {
            if (type.equals(wrapperClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Is this type a language construct (in `java.lang` package)?
     *
     * @param type The type to validate
     * @return Whether this class is in the `java.lang` package
     */
    protected boolean isLanguageConstruct(Class type) {
        return type.getName().startsWith("java.lang");
    }

    /**
     * Returns a trace of the dependencies.
     *
     * @return String builder, so it can be output.
     */
    protected StringBuilder getDependencyTrace() {
        StringBuilder builder = new StringBuilder();
        Class first = null;
        Class previous = null;

        for (Class current : currentlyResolving) {
            // store first entry
            if (first == null) {
                first = current;
            }

            // set previous to current entry and re-run
            if (previous == null) {
                previous = current;
                continue;
            }

            // add trace: previous > current
            builder.append(previous.getName());
            builder.append(" > ");
            builder.append(current.getName());
            builder.append("\n");

            // set previous to current
            previous = current;
        }

        // add trace: previous (last) > first
        if (previous != null) {
            builder.append(previous.getName());
            builder.append(" > ");
            builder.append(first.getName());
        }

        return builder;
    }

}
