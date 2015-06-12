package com.cookingfox.chefling;

import java.util.Map;

/**
 * A helper class that contains references to core Container elements.
 */
public class ContainerHelper {

    /**
     * A reference to the current Container instance.
     */
    public final Container container;

    /**
     * A reference to the current Container stored instances.
     */
    public final Map<Class, Object> instances;

    /**
     * A reference to the current Container mappings.
     */
    public final Map<Class, Object> mappings;

    /**
     * A reference to the current Container children.
     */
    public final ContainerChildren children;

    public ContainerHelper(Container container,
                           Map<Class, Object> instances,
                           Map<Class, Object> mappings,
                           ContainerChildren children) {
        this.container = container;
        this.instances = instances;
        this.mappings = mappings;
        this.children = children;
    }

}
