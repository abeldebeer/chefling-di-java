package com.cookingfox.chefling.api.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.LifeCycle;
import com.cookingfox.chefling.api.exception.ContainerException;

public interface CreateCommand {

    /**
     * Creates a new instance of `type`, attempting to resolve its full dependency tree. The
     * instance is not stored (that's what {@link Container#get(Class)} is for), so only use this
     * method directly when you need a NEW instance. It uses the type mappings (from the `map...`
     * methods) to create the instance. If no mapping is available, it attempts to resolve the
     * dependencies by inspecting the constructor parameters. If the created instance implements
     * {@link LifeCycle}, its {@link LifeCycle#initialize()} method will be called.
     *
     * @param type The type (class, interface) to instantiate.
     * @param <T>  Ensures the returned object is cast to the expected type.
     * @return Instance of `type`.
     * @throws ContainerException when the instance cannot be created, for example because its
     *                            dependencies cannot be resolved.
     */
    <T> T create(Class<T> type);

}
