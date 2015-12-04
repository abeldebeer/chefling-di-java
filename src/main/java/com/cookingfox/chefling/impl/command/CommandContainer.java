package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class CommandContainer implements Container {

    final Set<CommandContainer> children = new LinkedHashSet<>();
    final Map<Class, Object> instances = new LinkedHashMap<>();
    final Map<Class, Object> mappings = new LinkedHashMap<>();
    CommandContainer parent;

    final AddChildCommand addChildCommand;
    final CreateCommand createCommand;
    final GetCommand getCommand;
    final HasCommand hasCommand;
    final MapFactoryCommand mapFactoryCommand;
    final MapInstanceCommand mapInstanceCommand;
    final MapTypeCommand mapTypeCommand;
    final RemoveCommand removeCommand;
    final ResetCommand resetCommand;

    public CommandContainer() {
        addChildCommand = new AddChildCommand(this);
        createCommand = new CreateCommand(this);
        getCommand = new GetCommand(this);
        hasCommand = new HasCommand(this);
        mapFactoryCommand = new MapFactoryCommand(this);
        mapInstanceCommand = new MapInstanceCommand(this);
        mapTypeCommand = new MapTypeCommand(this);
        removeCommand = new RemoveCommand(this);
        resetCommand = new ResetCommand(this);

        reset();
    }

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
}
