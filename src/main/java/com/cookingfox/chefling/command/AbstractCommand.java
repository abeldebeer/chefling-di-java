package com.cookingfox.chefling.command;

import com.cookingfox.chefling.ContainerInterface;
import com.cookingfox.chefling.Factory;
import com.cookingfox.chefling.exception.TypeNotAllowedException;
import com.cookingfox.chefling.exception.TypeNotInstantiableException;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public abstract class AbstractCommand {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    protected final ContainerInterface container;

    protected final Map<Class, Object> instances;

    protected final Map<Class, Object> mappings;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public AbstractCommand(ContainerInterface container, Map<Class, Object> instances, Map<Class, Object> mappings) {
        this.container = container;
        this.instances = instances;
        this.mappings = mappings;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

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

}
