package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.LifeCycle;
import com.cookingfox.chefling.api.exception.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class CreateCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.CreateCommand {
    public CreateCommand(CommandContainer container) {
        super(container);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> type) throws ContainerException {
        assertNonNull(type, "type");
        isAllowed(type);

        Object mapping = findMapping(_container, type);
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
            parameters[i] = _container.get(parameterTypes[i]);
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
            if (_container.has(parameterType)) {
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
        Map<Integer, List<ResolvabilityResult>> resultMap = new TreeMap<Integer, List<ResolvabilityResult>>();

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
        errorBuilder.append("it does not have constructors that are resolvable by the Container:\n\n");

        Iterator iterator = resultMap.entrySet().iterator();

        while (iterator.hasNext()) {
            @SuppressWarnings("unchecked")
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
        errorBuilder.append(String.format("[%s] %s ( ", modifierName, type.getSimpleName()));

        Class[] parameterTypes = result.constructor.getParameterTypes();

        // add parameter types to constructor signature
        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameterType = parameterTypes[i];
            errorBuilder.append(parameterType.getName());

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
     * Resolves a type using a Factory instance. Throws if the returned value is null or invalid.
     *
     * @param factory The Factory object.
     * @param type    The expected type that the Factory should return.
     * @param <T>     Ensures the return value is cast to expected type.
     * @return The created instance.
     * @throws ContainerException
     */
    protected <T> T resolveUsingFactory(Factory<T> factory, Class<T> type) throws ContainerException {
        T instance = factory.create(_container);

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

        public final int parameterIndex;
        public final Exception exception;

        public UnresolvableParameter(int parameterIndex, Exception exception) {
            this.parameterIndex = parameterIndex;
            this.exception = exception;
        }

    }

    /**
     * Represents information for a constructor's resolvability.
     */
    protected static class ResolvabilityResult {

        public final Constructor constructor;
        public final int numParameters;
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
