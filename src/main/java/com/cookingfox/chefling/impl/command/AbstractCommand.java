package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.LifeCycle;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.chefling.impl.helper.Applier;
import com.cookingfox.chefling.impl.helper.Matcher;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base class for container commands used by the {@link CommandContainer}.
 */
abstract class AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    protected static final String PACKAGE_CHEFLING = "com.cookingfox.chefling";
    protected static final String PACKAGE_JAVA_LANG = "java.lang";

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * A reference to the container instance that this command is being applied to.
     */
    protected final CommandContainer _container;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public AbstractCommand(CommandContainer container) {
        _container = container;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Checks whether `type` is allowed and whether a mapping already exists. If everything is okay,
     * the mapping will be added, otherwise an exception will be thrown.
     *
     * @param type  The type to map.
     * @param value The value for the mapping.
     * @throws ContainerException
     */
    protected void addMapping(Class type, Object value) throws ContainerException {
        isAllowed(type);

        synchronized (_container) {
            if (_container.has(type)) {
                throw new TypeMappingAlreadyExistsException(type);
            }

            _container.mappings.put(type, value);
        }
    }

    /**
     * Apply an operation to the full tree of container children.
     *
     * @param container The container instance to use to get the root container.
     * @param applier   The operation to apply to all container children.
     */
    protected void applyAll(CommandContainer container, Applier applier) {
        applyRecursive(getRoot(container), applier);
    }

    /**
     * Apply an operation recursively to all children of the provided container instance.
     *
     * @param current The container to traverse the children of.
     * @param applier The operation to apply to all container children.
     */
    protected void applyRecursive(CommandContainer current, Applier applier) {
        applier.apply(current);

        for (CommandContainer child : current.children) {
            applyRecursive(child, applier);
        }
    }

    /**
     * Checks whether `value` is null, if so: throw an exception.
     *
     * @param value The value to check.
     * @param name  The name of the variable.
     * @throws NullValueNotAllowedException
     */
    protected void assertNonNull(Object value, String name) throws NullValueNotAllowedException {
        if (value == null) {
            throw new NullValueNotAllowedException(name);
        }
    }

    /**
     * Checks for mapping conflicts between the current container and the provided container
     * instance. Throws when there is a duplicate mapping or instance.
     *
     * @param container The new container.
     * @throws ContainerException
     */
    protected void checkMappingConflicts(CommandContainer container) throws ContainerException {
        for (Class type : compileTypes(container)) {
            if (_container.has(type)) {
                throw new ContainerException("Mapping conflict: new container already contains " +
                        "a configuration for type: " + type);
            }
        }
    }

    /**
     * Generate a set of types for all instances and types of the full tree of container children.
     *
     * @param container The source container.
     * @return Set of all types within the container.
     */
    protected Set<Class> compileTypes(CommandContainer container) {
        final Set<Class> types = new LinkedHashSet<>();

        applyAll(container, new Applier() {
            @Override
            public void apply(CommandContainer container) {
                types.addAll(container.instances.keySet());
                types.addAll(container.mappings.keySet());
            }
        });

        // remove the default mappings
        types.remove(Container.class);
        types.remove(CommandContainer.class);

        return types;
    }

    /**
     * Traverse through the full tree of container children and return all containers that match.
     *
     * @param start   A reference container to use to get the root.
     * @param matcher The matcher to check for a certain condition.
     * @return A set of containers that match.
     */
    protected Set<CommandContainer> findAll(CommandContainer start, Matcher matcher) {
        final Set<CommandContainer> result = new LinkedHashSet<>();

        findAllRecursive(result, getRoot(start), matcher);

        return result;
    }

    /**
     * Recursively traverse the current container's children and add matches to the result set.
     *
     * @param result  The result set to add matching containers to.
     * @param current The current container instance to traverse the children of.
     * @param matcher The matcher to check for a certain condition.
     */
    protected void findAllRecursive(final Set<CommandContainer> result, CommandContainer current, Matcher matcher) {
        if (matcher.matches(current)) {
            result.add(current);
        }

        for (CommandContainer child : current.children) {
            findAllRecursive(result, child, matcher);
        }
    }

    /**
     * Find an instance or type mapping for the provided type in the tree of container children.
     *
     * @param container The container to use to get the root.
     * @param type      The type to check for.
     * @return The instance or type mapping from one of the container children, or null if not found.
     */
    protected Object findMapping(CommandContainer container, Class type) {
        CommandContainer match = findOne(container, HasMappingMatcher.get(type));

        if (match == null) {
            return null;
        }

        Object instance = match.instances.get(type);

        return instance != null ? instance : match.mappings.get(type);
    }

    /**
     * Find one container in the full tree of children by performing a matching operation on each
     * child.
     *
     * @param target  The target container to use to get the root.
     * @param matcher The matcher operation to use for each container child.
     * @return The matching container or null.
     */
    protected CommandContainer findOne(CommandContainer target, Matcher matcher) {
        return findOneRecursive(getRoot(target), matcher);
    }

    protected CommandContainer findOneRecursive(CommandContainer current, Matcher matcher) {
        if (matcher.matches(current)) {
            return current;
        }

        for (CommandContainer child : current.children) {
            CommandContainer match = findOneRecursive(child, matcher);

            if (match != null) {
                return match;
            }
        }

        return null;
    }

    /**
     * Recursively find the root container, which is the container instance that has no parent set.
     *
     * @param current The current container instance in the recursive operation.
     * @return The root container.
     */
    protected CommandContainer getRoot(CommandContainer current) {
        return current.parent == null ? current : getRoot(current.parent);
    }

    /**
     * Is this type allowed to be mapped in the Container?
     *
     * @param type The type to validate.
     * @throws TypeNotAllowedException
     */
    protected void isAllowed(Class type) throws TypeNotAllowedException {
        String errorReason = null;
        int modifiers = type.getModifiers();

        if (isInPackage(type, PACKAGE_JAVA_LANG)) {
            errorReason = "a Java language construct";
        } else if (isInPackage(type, PACKAGE_CHEFLING)) {
            errorReason = "part of the Chefling library";
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
     * Is this type in the provided package?
     *
     * @param type The type to check.
     * @param pkg  The root package name.
     * @return Whether this type is in the provided package.
     */
    protected boolean isInPackage(Class type, String pkg) {
        return type.getName().startsWith(pkg);
    }

    /**
     * Call the {@link LifeCycle#onDestroy()} method if the object is a {@link LifeCycle} instance.
     *
     * @param instance An object.
     */
    protected void lifeCycleDestroy(Object instance) {
        if (instance instanceof LifeCycle) {
            ((LifeCycle) instance).onDestroy();
        }
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * A {@link Matcher} implementation that returns true if the provided container has an instance
     * mapping for the provided type. This is an optimization: by using
     * {@link HasMappingMatcher#get(Class)} we can re-use the matcher instance.
     */
    protected static class HasMappingMatcher implements Matcher {

        /**
         * The matcher instance to re-use.
         */
        protected static HasMappingMatcher instance = new HasMappingMatcher();

        /**
         * The type to match.
         */
        Class type;

        /**
         * Constructor is disabled: use {@link #get(Class)}.
         */
        private HasMappingMatcher() {
        }

        @Override
        public boolean matches(CommandContainer container) {
            return container.instances.containsKey(type) || container.mappings.containsKey(type);
        }

        /**
         * Get the matcher instance for the provided type.
         *
         * @param type The type to match.
         * @return The matcher instance.
         */
        public static HasMappingMatcher get(Class type) {
            instance.type = type;

            return instance;
        }

    }

}
