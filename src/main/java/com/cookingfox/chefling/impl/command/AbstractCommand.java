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
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
abstract class AbstractCommand {
    final CommandContainer _container;

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

    protected void checkMappingConflicts(CommandContainer container) throws ContainerException {
        for (Class type : compileTypes(container)) {
            if (_container.has(type)) {
                throw new ContainerException("Mapping conflict: new container already contains " +
                        "a configuration for type: " + type);
            }
        }
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

        if (isInPackage(type, "java.lang")) {
            errorReason = "a Java language construct";
        } else if (isInPackage(type, "com.cookingfox.chefling")) {
            errorReason = "part of the Chefling library";
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
    // STATIC METHODS
    //----------------------------------------------------------------------------------------------

    protected static void applyAll(CommandContainer container, Applier applier) {
        CommandContainer root = getRoot(container);

        applyRecursive(root, applier);
    }

    private static void applyRecursive(CommandContainer container, Applier applier) {
        applier.apply(container);

        for (CommandContainer child : container.children) {
            applier.apply(child);

            applyRecursive(child, applier);
        }
    }

    protected static Set<Class> compileTypes(CommandContainer container) {
        final Set<Class> types = new LinkedHashSet<>();

        applyAll(container, new Applier() {
            @Override
            public void apply(CommandContainer container) {
                types.addAll(container.instances.keySet());
                types.addAll(container.mappings.keySet());
            }
        });

        types.remove(Container.class);
        types.remove(CommandContainer.class);

        return types;
    }

    protected static Object findMapping(CommandContainer container, final Class type) {
        CommandContainer match = find(container, HasMappingMatcher.get(type));

        if (match == null) {
            return null;
        }

        Object instance = match.instances.get(type);

        return instance != null ? instance : match.mappings.get(type);
    }

    protected static CommandContainer find(final CommandContainer target) {
        return find(target, new Matcher() {
            @Override
            public boolean matches(CommandContainer container) {
                return container.equals(target);
            }
        });
    }

    protected static CommandContainer find(CommandContainer target, Matcher matcher) {
        CommandContainer root = getRoot(target);

        return findRecursive(root, matcher);
    }

    private static CommandContainer findRecursive(CommandContainer container, Matcher matcher) {
        if (matcher.matches(container)) {
            return container;
        }

        for (CommandContainer child : container.children) {
            CommandContainer match = findRecursive(child, matcher);

            if (match != null) {
                return match;
            }
        }

        return null;
    }

    protected static CommandContainer getRoot(CommandContainer container) {
        if (container.parent == null) {
            return container;
        }

        return getRoot(container.parent);
    }

    protected static Set<CommandContainer> findAll(CommandContainer start, Matcher matcher) {
        final Set<CommandContainer> result = new LinkedHashSet<>();
        final CommandContainer root = getRoot(start);

        findAllRecursive(result, root, matcher);

        return result;
    }

    private static void findAllRecursive(final Set<CommandContainer> result, CommandContainer current, Matcher matcher) {
        if (matcher.matches(current)) {
            result.add(current);
        }

        for (CommandContainer child : current.children) {
            findAllRecursive(result, child, matcher);
        }
    }

    //----------------------------------------------------------------------------------------------
    // INNER CLASSES
    //----------------------------------------------------------------------------------------------

    static class HasMappingMatcher implements Matcher {
        private static HasMappingMatcher instance = new HasMappingMatcher();

        Class type;

        private HasMappingMatcher() {
        }

        @Override
        public boolean matches(CommandContainer container) {
            return container.instances.containsKey(type) || container.mappings.containsKey(type);
        }

        public static HasMappingMatcher get(Class type) {
            instance.type = type;

            return instance;
        }
    }

}
