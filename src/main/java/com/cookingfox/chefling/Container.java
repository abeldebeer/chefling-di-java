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
    protected Map<Class, Object> instances;

    /**
     * Temporary 'log' of the types that are in the process of being resolved, where key is the type
     * that is requested and value is the type that is being resolved. After the type is
     * successfully resolved, the entry is removed from the map. This map is used to detect circular
     * dependencies.
     */
    protected Map<Class, Class> resolving;

    /**
     * Type map, where key is the type that is requested and value is the sub type that is created.
     */
    protected Map<Class, Class> subTypes;

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
        typeIsAllowed(type);
        typeIsInstantiable(subType);

        subTypes.put(type, subType);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#set(Class, Object)
     */
    @Override
    public <Type> void set(Class<Type> type, Type instance)
            throws NotAnInstanceOfTypeException, TypeNotAllowedException {
        typeIsAllowed(type);

        // validate the instance is an instance of type
        if (!type.isInstance(instance)) {
            throw new NotAnInstanceOfTypeException(type, instance);
        }

        instances.put(type, instance);
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
        typeIsInstantiable(type);

        Constructor[] constructors = type.getDeclaredConstructors();
        Constructor constructor = null;

        for (Constructor current : constructors) {
            // is the constructor public?
            if (Modifier.isPublic(current.getModifiers())) {
                if (constructor == null) {
                    constructor = current;
                }

                // constructor has no parameters? select it.
                if (current.getParameterTypes().length == 0) {
                    constructor = current;
                    break;
                }
            }
        }

        if (constructor == null) {
            throw new TypeNotInstantiableException(type, "its constructor is not public");
        }

        return constructor;
    }

    /**
     * Validates the type and throws if it is not allowed.
     *
     * @param type The type to validate.
     * @throws TypeNotAllowedException
     */
    protected void typeIsAllowed(Class type) throws TypeNotAllowedException {
        String errorReason = null;
        int modifiers = type.getModifiers();

        if (type.equals(Object.class)) {
            errorReason = "the root class of every object";
        } else if (type.equals(Class.class)) {
            errorReason = "the root class of every type";
        } else if (type.equals(String.class)) {
            errorReason = "considered primitive by the container";
        } else if (type.isPrimitive()) {
            errorReason = "primitive";
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
     * Validates the type and throws if it is not allowed or not instantiable.
     *
     * @param type The type to validate.
     * @throws TypeNotAllowedException
     */
    protected void typeIsInstantiable(Class type) throws TypeNotAllowedException {
        typeIsAllowed(type);

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
