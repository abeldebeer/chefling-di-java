package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    protected HashMap<Class, Object> instances;

    /**
     * Temporary 'log' of the types that are in the process of being resolved, where key is the type
     * that is requested and value is the type that is being resolved. After the type is
     * successfully resolved, the entry is removed from the map. This map is used to detect circular
     * dependencies.
     */
    protected LinkedHashMap<Class, Class> resolving;

    /**
     * Type map, where key is the type that is requested and value is the sub type that is created.
     */
    protected HashMap<Class, Class> subTypes;

    /**
     * Convenience singleton for apps using a process-wide Container instance.
     */
    protected static volatile Container defaultInstance;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public Container() {
        instances = new HashMap<Class, Object>();
        resolving = new LinkedHashMap<Class, Class>();
        subTypes = new HashMap<Class, Class>();

        // map this container instance to its class and interface, so clients requesting it will
        // receive this instance
        instances.put(Container.class, this);
        instances.put(ContainerInterface.class, this);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see com.cookingfox.chefling.ContainerInterface#create(Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <Type> Type create(Class<Type> type)
            throws CircularDependencyDetectedException, TypeInstantiationException, TypeNotAllowedException {
        Constructor constructor = getDefaultConstructor(type);
        Class[] parameterTypes = constructor.getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];

        // gather constructor parameters based on their types
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = get(parameterTypes[i]);
        }

        try {
            // create a new instance, passing the constructor parameters
            return (Type) constructor.newInstance(parameters);
        } catch (Exception e) {
            throw new TypeInstantiationException(type, e);
        }
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#get(Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized <Type> Type get(Class<Type> type)
            throws CircularDependencyDetectedException, TypeInstantiationException, TypeNotAllowedException {
        Type instance = (Type) instances.get(type);

        if (instance != null) {
            return instance;
        }

        Class<Type> typeToCreate = type;

        // if this type has been mapped to another type (using the map() method), use the sub
        // type to create the instance
        if (subTypes.containsKey(type)) {
            typeToCreate = subTypes.get(type);
        }

        // if the requested type is already being processed, it indicates a circular dependency
        if (resolving.containsKey(type)) {
            throw new CircularDependencyDetectedException(getDependencyTrace());
        } else {
            // store currently processed type
            resolving.put(type, typeToCreate);
        }

        try {
            // create and store instance
            instance = create(typeToCreate);
            instances.put(type, instance);
        } finally {
            // remove processed type
            resolving.remove(type);
        }

        return instance;
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#has(Class)
     */
    @Override
    public boolean has(Class type) {
        return instances.containsKey(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#map(Class, Class)
     */
    @Override
    public synchronized <Type> void map(Class<Type> type, Class<? extends Type> subType)
            throws NotASubTypeException, TypeMappingAlreadyExistsException, TypeNotAllowedException {
        // check whether a mapping already exists
        if (subTypes.containsKey(type)) {
            throw new TypeMappingAlreadyExistsException(type);
        }

        // validate the sub type extends the type
        if (subType.equals(type) || !type.isAssignableFrom(subType)) {
            throw new NotASubTypeException(type, subType);
        }

        // validate the types
        isAllowed(type);
        isInstantiable(subType);

        subTypes.put(type, subType);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#set(Class, Object)
     */
    @Override
    public <Type> void set(Class<Type> type, Type instance)
            throws NotAnInstanceOfTypeException, TypeNotAllowedException {
        // validate the instance is an instance of type
        if (!type.isInstance(instance)) {
            throw new NotAnInstanceOfTypeException(type, instance);
        }

        isAllowed(type);

        instances.put(type, instance);
    }

    /**
     * Convenience singleton for apps using a process-wide EventBus instance.
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

        for (Constructor current : constructors) {
            // constructor not public? skip
            if (!Modifier.isPublic(current.getModifiers())) {
                continue;
            }

            selectedConstructor = selectConstructorBasedOnParameters(current);

            if (selectedConstructor != null) {
                break;
            }
        }

        if (selectedConstructor == null) {
            throw new TypeNotInstantiableException(type, "its constructor is not public or its parameters are not resolvable by the container");
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
            if (subTypes.containsKey(parameterType) || instances.containsKey(parameterType)) {
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

        if (isLanguageConstruct(type)) {
            errorReason = "a Java language construct";
        } else if (isPrimitiveOrWrapper(type)) {
            errorReason = "primitive";
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
     * Is this type a language construct (in `java.lang` package)?
     *
     * @param type The type to validate
     * @return Whether this class is in the `java.lang` package
     */
    protected boolean isLanguageConstruct(Class type) {
        // use java.lang.Object class as example
        return type.getName().startsWith(Object.class.getPackage().getName());
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
     * Returns a trace of the dependencies.
     *
     * @return String builder, so it can be output.
     */
    protected StringBuilder getDependencyTrace() {
        StringBuilder builder = new StringBuilder();
        Map.Entry<Class, Class> first = null;
        Map.Entry<Class, Class> previous = null;

        for (Map.Entry<Class, Class> current : resolving.entrySet()) {
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
            addDependencyTrace(builder, previous);
            builder.append(" > ");
            addDependencyTrace(builder, current);
            builder.append("\n");

            // set previous to current
            previous = current;
        }

        // add trace: previous (last) > first
        addDependencyTrace(builder, previous);
        builder.append(" > ");
        addDependencyTrace(builder, first);

        return builder;
    }

    /**
     * Add one dependency trace to the string builder.
     *
     * @param builder The string builder.
     * @param entry   The current dependency entry.
     */
    protected void addDependencyTrace(StringBuilder builder, Map.Entry<Class, Class> entry) {
        Class requestedType = entry.getKey();
        Class typeToCreate = entry.getValue();

        builder.append(typeToCreate.getName());

        // add requested name for reference
        if (!requestedType.equals(typeToCreate)) {
            builder.append(" (");
            builder.append(requestedType.getName());
            builder.append(")");
        }
    }

}
