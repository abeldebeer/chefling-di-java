package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.LifeCycle;
import com.cookingfox.chefling.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

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

        if (mapping instanceof Class) {
            // create instance using mapped type
            instance = create((Class<T>) mapping);
        } else if (type.isInstance(mapping)) {
            // mapping is instance
            instance = (T) mapping;
        } else if (mapping instanceof Factory) {
            // use factory to create instance
            instance = resolveUsingFactory((Factory<T>) mapping, type);
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
     * Get the default constructor for this type.
     *
     * @param type The type to get the constructor for.
     * @return Constructor, if a resolvable one can be found.
     * @throws TypeNotAllowedException
     */
    protected Constructor getDefaultConstructor(Class type) throws TypeNotAllowedException {
        isInstantiable(type);

        Constructor[] constructors = type.getDeclaredConstructors();
        ResolvabilityResult firstResult = getResolvabilityResult(constructors[0]);

        // if first constructor is resolvable, return it immediately
        if (firstResult.isResolvable()) {
            return firstResult.constructor;
        }

        // map of resolvable results, by number of parameters: we favor a constructor with a small
        // number of parameters, because the chances are higher that it is resolvable.
        TreeMap<Integer, List<ResolvabilityResult>> resultMap = new TreeMap<Integer, List<ResolvabilityResult>>();

        // inspect constructor resolvability
        for (Constructor constructor : constructors) {
            // create a resolvability result for this constructor
            ResolvabilityResult result = getResolvabilityResult(constructor);
            List<ResolvabilityResult> resultList = resultMap.get(result.numParameters);

            if (resultList == null) {
                resultList = new LinkedList<ResolvabilityResult>();
            }

            resultList.add(result);
            resultMap.put(result.numParameters, resultList);
        }

        // select resolvable constructor
        for (Map.Entry<Integer, List<ResolvabilityResult>> entry : resultMap.entrySet()) {
            for (ResolvabilityResult result : entry.getValue()) {
                if (result.isResolvable()) {
                    // constructor is resolvable: return it
                    return result.constructor;
                }
            }
        }

        // builder error message
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("it does not have constructors that are resolvable by the Container:\n\n");

        // create resolvability report for unresolvable type
        for (Map.Entry<Integer, List<ResolvabilityResult>> entry : resultMap.entrySet()) {
            // add error report entry for every resolvability result
            for (ResolvabilityResult result : entry.getValue()) {
                addErrorReportEntry(errorBuilder, result, type);
            }
        }

        throw new TypeNotInstantiableException(type, errorBuilder.toString());
    }

    /**
     * Create a "resolvability" result: check all constructor parameters to see whether they are
     * resolvable by the Container.
     *
     * @param constructor The constructor to check.
     * @return The result.
     */
    protected ResolvabilityResult getResolvabilityResult(Constructor constructor) {
        Class[] parameterTypes = constructor.getParameterTypes();
        int numParameters = parameterTypes.length;
        ResolvabilityResult result = new ResolvabilityResult(constructor, numParameters);

        // check whether the constructor parameters are resolvable
        for (int i = 0; i < numParameters; i++) {
            Class parameterType = parameterTypes[i];

            // container has a mapping for this parameter type: ok!
            if (container.has(parameterType)) {
                continue;
            }

            try {
                // is this type instantiable?
                isInstantiable(parameterType);
            } catch (TypeNotAllowedException e) {
                // not instantiable: store in result
                result.unresolvable.add(new UnresolvableParameter(i, e));
            }
        }

        return result;
    }

    /**
     * Add an error report for an unresolvable constructor.
     *
     * @param errorBuilder The string builder for the error message.
     * @param result       The resolvability result.
     * @param type         The type we are attempting to instantiate.
     */
    protected void addErrorReportEntry(StringBuilder errorBuilder, ResolvabilityResult result, Class type) {
        // add name of this constructor
        String modifierName = Modifier.toString(result.getModifiers());
        errorBuilder.append(String.format("[%s] %s ( ", modifierName, type.getSimpleName()));

        Class[] parameterTypes = result.constructor.getParameterTypes();

        // add parameter types to constructor signature
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            errorBuilder.append(parameterType.getSimpleName());

            if (i < parameterTypes.length - 1) {
                errorBuilder.append(", ");
            }
        }

        errorBuilder.append(" )\n");

        if (!result.unresolvable.isEmpty()) {
            // loop through unresolvable parameters and print their exception messages
            for (UnresolvableParameter notResolvable : result.unresolvable) {
                errorBuilder.append(String.format("Parameter #%d: %s\n",
                        notResolvable.parameterIndex + 1, notResolvable.exception.getMessage()));
            }

            errorBuilder.append("\n");
        }
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

    //----------------------------------------------------------------------------------------------
    // INTERNAL CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * Represents information for an unresolvable constructor parameter.
     */
    protected static class UnresolvableParameter {

        public int parameterIndex;
        public Exception exception;

        public UnresolvableParameter(int parameterIndex, Exception exception) {
            this.parameterIndex = parameterIndex;
            this.exception = exception;
        }

    }

    /**
     * Represents information for a constructor's resolvability.
     */
    protected static class ResolvabilityResult {

        public Constructor constructor;
        int numParameters;
        public final ArrayList<UnresolvableParameter> unresolvable = new ArrayList<UnresolvableParameter>();

        public ResolvabilityResult(Constructor constructor, int numParameters) {
            this.constructor = constructor;
            this.numParameters = numParameters;
        }

        public int getModifiers() {
            return constructor.getModifiers();
        }

        public boolean isPublic() {
            return Modifier.isPublic(getModifiers());
        }

        public boolean isResolvable() {
            return isPublic() && unresolvable.isEmpty();
        }

    }

}
