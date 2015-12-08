package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
    protected final Set<CommandContainer> children = new LinkedHashSet<>();

    /**
     * Stores created instances, where the key is the type and the value is the instance. This
     * instance is returned the next time the type is requested.
     */
    protected final Map<Class, Object> instances = new LinkedHashMap<>();

    /**
     * Stores type mappings, where the key is the type and the value is the mapping provided by the
     * `map...` methods.
     */
    protected final Map<Class, Object> mappings = new LinkedHashMap<>();

    /**
     * The parent container configuration.
     */
    protected CommandContainer parent;

    //----------------------------------------------------------------------------------------------
    // COMMAND INSTANCES
    //----------------------------------------------------------------------------------------------

    protected final AddChildCommand addChildCommand = new AddChildCommand(this);
    protected final CreateCommand createCommand = new CreateCommand(this);
    protected final GetCommand getCommand = new GetCommand(this);
    protected final HasCommand hasCommand = new HasCommand(this);
    protected final MapFactoryCommand mapFactoryCommand = new MapFactoryCommand(this);
    protected final MapInstanceCommand mapInstanceCommand = new MapInstanceCommand(this);
    protected final MapTypeCommand mapTypeCommand = new MapTypeCommand(this);
    protected final RemoveCommand removeCommand = new RemoveCommand(this);
    protected final ResetCommand resetCommand = new ResetCommand(this);
    protected final SetParentCommand setParentCommand = new SetParentCommand(this);

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
    public void addChild(Container container) throws ContainerException {
        addChildCommand.addChild(container);
    }

    @Override
    public <T> T create(Class<T> type) throws ContainerException {
        return createCommand.create(type);
    }

    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        return getCommand.get(type);
    }

    @Override
    public boolean has(Class type) {
        return hasCommand.has(type);
    }

    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        mapFactoryCommand.mapFactory(type, factory);
    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {
        mapInstanceCommand.mapInstance(type, instance);
    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {
        mapTypeCommand.mapType(type, subType);
    }

    @Override
    public void remove(Class type) throws ContainerException {
        removeCommand.remove(type);
    }

    @Override
    public void reset() {
        resetCommand.reset();
    }

    @Override
    public void setParent(Container container) throws ContainerException {
        setParentCommand.setParent(container);
    }

}
