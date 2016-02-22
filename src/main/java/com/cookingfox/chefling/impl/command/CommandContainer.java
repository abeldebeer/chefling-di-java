package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.command.*;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * {@link Container} implementation that uses command classes for each container operation.
 */
public class CommandContainer implements Container {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * A collection of child container configurations.
     */
    protected final LinkedHashSet<CommandContainer> children = new LinkedHashSet<>();

    /**
     * Stores created instances, where the key is the type and the value is the instance. This
     * instance is returned the next time the type is requested.
     */
    protected final LinkedHashMap<Class, Object> instances = new LinkedHashMap<>();

    /**
     * Stores type mappings, where the key is the type and the value is the mapping provided by the
     * `map...` methods.
     */
    protected final LinkedHashMap<Class, Object> mappings = new LinkedHashMap<>();

    /**
     * The parent container configuration.
     */
    protected CommandContainer parent;

    //----------------------------------------------------------------------------------------------
    // COMMAND INSTANCES
    //----------------------------------------------------------------------------------------------

    protected final AddChildCommand addChildCommand = new AddChildCommandImpl(this);
    protected final CreateCommand createCommand = new CreateCommandImpl(this);
    protected final GetCommand getCommand = new GetCommandImpl(this);
    protected final HasCommand hasCommand = new HasCommandImpl(this);
    protected final MapFactoryCommand mapFactoryCommand = new MapFactoryCommandImpl(this);
    protected final MapInstanceCommand mapInstanceCommand = new MapInstanceCommandImpl(this);
    protected final MapTypeCommand mapTypeCommand = new MapTypeCommandImpl(this);
    protected final RemoveCommand removeCommand = new RemoveCommandImpl(this);
    protected final ResetCommand resetCommand = new ResetCommandImpl(this);
    protected final SetParentCommand setParentCommand = new SetParentCommandImpl(this);

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public CommandContainer() {
        reset();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addChild(Container container) {
        addChildCommand.addChild(container);
    }

    @Override
    public <T> T create(Class<T> type) {
        return createCommand.create(type);
    }

    @Override
    public <T> T get(Class<T> type) {
        return getCommand.get(type);
    }

    @Override
    public boolean has(Class type) {
        return hasCommand.has(type);
    }

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) {
        mapFactoryCommand.mapFactory(type, factory);
    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) {
        mapInstanceCommand.mapInstance(type, instance);
    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) {
        mapTypeCommand.mapType(type, subType);
    }

    @Override
    public void remove(Class type) {
        removeCommand.remove(type);
    }

    @Override
    public void reset() {
        resetCommand.reset();
    }

    @Override
    public void setParent(Container container) {
        setParentCommand.setParent(container);
    }

}
