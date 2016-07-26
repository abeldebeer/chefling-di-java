package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.chefling.api.command.CreateInstanceCommand;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.chefling.impl.helper.ConstructorParameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @see CreateInstanceCommand
 */
public class CreateInstanceCommandImpl extends AbstractCommand implements CreateInstanceCommand {

    //----------------------------------------------------------------------------------------------
    // STATIC PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Cache for selected constructor + parameter types, since this is an expensive operation.
     */
    protected final static LinkedHashMap<Class, ConstructorParameters> TYPE_CACHE = new LinkedHashMap<>();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public CreateInstanceCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createInstance(Class<T> type) {
        assertNonNull(type, "type");
        isAllowed(type);

        final Object existing = findInstanceOrMapping(_container, type);
        T instance;

        if (existing instanceof Class) {
            // type is mapped to another type: create an instance using the mapped type
            instance = createInstance((Class<T>) existing);
        } else if (type.isInstance(existing)) {
            // existing is instance
            instance = (T) existing;
        } else if (existing instanceof CheflingFactory) {
            // use factory to create instance
            instance = resolveUsingFactory((CheflingFactory<T>) existing, type);
        } else {
            // no existing mapping or instance: create instance using provided type
            instance = constructInstance(type);
        }

        // call life cycle initialize
        if (instance instanceof CheflingLifecycle) {
            ((CheflingLifecycle) instance).initialize();
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
    protected <T> T constructInstance(Class<T> type) {
        // cached constructor + parameters for this type
        ConstructorParameters cached = TYPE_CACHE.get(type);

        // resolve using cache
        if (cached != null) {
            return newInstance(type, cached.constructor, cached.parameterTypes);
        }

        // determine default constructor + parameters
        Constructor constructor = getDefaultConstructor(type);
        Class[] parameterTypes = constructor.getParameterTypes();

        // create instance
        T instance = newInstance(type, constructor, parameterTypes);

        // success: cache constructor + parameters for this type
        TYPE_CACHE.put(type, new ConstructorParameters(constructor, parameterTypes));

        return instance;
    }

    /**
     * Creates a new instance by providing the correct parameters to the selected constructor.
     *
     * @param type           The type to instantiate.
     * @param constructor    The selected constructor for this type.
     * @param parameterTypes The parameter types of the constructor.
     * @param <T>            Ensures the returned object is cast to the expected type.
     * @return New instance of the type.
     * @throws ContainerException
     */
    @SuppressWarnings("unchecked")
    protected <T> T newInstance(Class<T> type, Constructor constructor, Class[] parameterTypes)
            throws ContainerException {
        Object[] parameters = new Object[parameterTypes.length];

        // gather constructor parameters based on their types
        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = _container.getInstance(parameterTypes[i]);
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
        Map<Integer, List<ResolvabilityResult>> resultMap = buildResultMap(constructors);

        // select resolvable constructor
        for (Map.Entry<Integer, List<ResolvabilityResult>> entry : resultMap.entrySet()) {
            for (ResolvabilityResult result : entry.getValue()) {
                if (result.isResolvable()) {
                    // constructor is resolvable: return it
                    return result.constructor;
                }
            }
        }

        throw new TypeNotInstantiableException(type, buildErrorMessage(type, resultMap));
    }

    /**
     * Create a "resolvability" result: check all constructor parameters to see whether they are
     * resolvable by the container.
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
            if (_container.hasInstanceOrMapping(parameterType)) {
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
     * Build a resolvability result map, sorted by number of parameters.
     *
     * @param constructors Declared constructors for the type that needs to be created.
     * @return A map of resolvability results.
     */
    protected Map<Integer, List<ResolvabilityResult>> buildResultMap(Constructor[] constructors) {
        Map<Integer, List<ResolvabilityResult>> resultMap = new TreeMap<>();

        // inspect constructor resolvability
        for (Constructor constructor : constructors) {
            // create a resolvability result for this constructor
            ResolvabilityResult result = getResolvabilityResult(constructor);
            List<ResolvabilityResult> resultList = resultMap.get(result.numParameters);

            if (resultList == null) {
                resultList = new LinkedList<>();
            }

            resultList.add(result);
            resultMap.put(result.numParameters, resultList);
        }

        return resultMap;
    }

    /**
     * Builds an error message that is thrown when the type is not instantiable. It uses the
     * information from the class resolvability results.
     *
     * @param type      The type to get the constructor for.
     * @param resultMap The results from the resolvability checks.
     * @return The error message.
     */
    protected String buildErrorMessage(Class type, Map<Integer, List<ResolvabilityResult>> resultMap) {
        // build error message
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("it does not have constructors that are resolvable by the container:\n\n");

        Iterator iterator = resultMap.entrySet().iterator();

        while (iterator.hasNext()) {
            // noinspection unchecked
            Map.Entry<Integer, List<ResolvabilityResult>> entry = (Map.Entry) iterator.next();
            List<ResolvabilityResult> resultList = entry.getValue();

            // add error report entry for every resolvability result
            for (int i = 0; i < resultList.size(); i++) {
                addErrorReportEntry(errorBuilder, resultList.get(i), type);

                if (i < resultList.size() - 1) {
                    errorBuilder.append("\n");
                }
            }

            if (iterator.hasNext()) {
                errorBuilder.append("\n");
            }

            iterator.remove();
        }

        return errorBuilder.toString();
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

        if (modifierName.isEmpty()) {
            modifierName = "non-public";
        }

        errorBuilder.append(String.format("[%s] %s ( ", modifierName, type.getSimpleName()));

        Class[] parameterTypes = result.constructor.getParameterTypes();

        // add parameter types to constructor signature
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            errorBuilder.append(parameterType.getName());

            // add separator between parameter types
            if (i < parameterTypes.length - 1) {
                errorBuilder.append(", ");
            }
        }

        errorBuilder.append(" )\n");

        if (!result.isPublic()) {
            errorBuilder.append(String.format("The constructor is %s\n", modifierName));
        } else if (!result.unresolvable.isEmpty()) {
            // loop through unresolvable parameters and print their exception messages
            for (UnresolvableParameter notResolvable : result.unresolvable) {
                errorBuilder.append(String.format("Parameter #%d: %s\n",
                        notResolvable.parameterIndex + 1, notResolvable.exception.getMessage()));
            }
        }
    }

    /**
     * Resolves a type using a factory instance. Throws if the returned value is null or invalid.
     *
     * @param factory The factory object.
     * @param type    The expected type that the factory should return.
     * @param <T>     Ensures the return value is cast to expected type.
     * @return The created instance.
     * @throws ContainerException
     */
    protected <T> T resolveUsingFactory(CheflingFactory<T> factory, Class<T> type) {
        T instance = factory.createInstance(_container);

        if (instance == null) {
            throw new FactoryReturnedNullException(type);
        } else if (!type.isInstance(instance)) {
            throw new FactoryReturnedUnexpectedValueException(type, instance);
        }

        return instance;
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * Represents information for an unresolvable constructor parameter.
     */
    protected static class UnresolvableParameter {

        protected final int parameterIndex;
        protected final Exception exception;

        protected UnresolvableParameter(int parameterIndex, Exception exception) {
            this.parameterIndex = parameterIndex;
            this.exception = exception;
        }

    }

    /**
     * Represents information for a constructor's resolvability.
     */
    protected static class ResolvabilityResult {

        protected final Constructor constructor;
        protected final int numParameters;
        protected final ArrayList<UnresolvableParameter> unresolvable = new ArrayList<>();

        protected ResolvabilityResult(Constructor constructor, int numParameters) {
            this.constructor = constructor;
            this.numParameters = numParameters;
        }

        protected int getModifiers() {
            return constructor.getModifiers();
        }

        protected boolean isPublic() {
            return Modifier.isPublic(getModifiers());
        }

        protected boolean isResolvable() {
            return isPublic() && unresolvable.isEmpty();
        }

    }

}
