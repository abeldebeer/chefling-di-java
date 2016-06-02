package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.command.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * {@link CheflingContainer} implementation that uses command classes for each container operation.
 */
public class CommandContainer implements CheflingContainer {

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
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

    protected AddChildContainerCommand addChildContainer = new AddChildContainerCommandImpl(this);
    protected CreateChildContainerCommand createChild = new CreateChildContainerCommandImpl(this);
    protected CreateInstanceCommand createInstance = new CreateInstanceCommandImpl(this);
    protected GetInstanceCommand getInstance = new GetInstanceCommandImpl(this);
    protected HasInstanceOrMappingCommand hasInstanceOrMapping = new HasInstanceOrMappingCommandImpl(this);
    protected MapFactoryCommand mapFactory = new MapFactoryCommandImpl(this);
    protected MapInstanceCommand mapInstance = new MapInstanceCommandImpl(this);
    protected MapTypeCommand mapType = new MapTypeCommandImpl(this);
    protected RemoveInstanceAndMappingCommand removeInstanceAndMapping = new RemoveInstanceAndMappingCommandImpl(this);
    protected ResetContainerCommand resetContainer = new ResetContainerCommandImpl(this);
    protected SetParentContainerCommand setParentContainer = new SetParentContainerCommandImpl(this);
    protected ValidateContainerCommand validateContainer = new ValidateContainerCommandImpl(this);

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public CommandContainer() {
        resetContainer();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void addChildContainer(CheflingContainer container) {
        addChildContainer.addChildContainer(container);
    }

    @Override
    public <T> T createInstance(Class<T> type) {
        return createInstance.createInstance(type);
    }

    @Override
    public CheflingContainer createChildContainer() {
        return createChild.createChildContainer();
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return getInstance.getInstance(type);
    }

    @Override
    public boolean hasInstanceOrMapping(Class type) {
        return hasInstanceOrMapping.hasInstanceOrMapping(type);
    }

    @Override
    public <T> void mapFactory(Class<T> type, CheflingFactory<T> factory) {
        mapFactory.mapFactory(type, factory);
    }

    @Override
    public <T> void mapInstance(Class<T> type, T instance) {
        mapInstance.mapInstance(type, instance);
    }

    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) {
        mapType.mapType(type, subType);
    }

    @Override
    public void removeInstanceAndMapping(Class type) {
        removeInstanceAndMapping.removeInstanceAndMapping(type);
    }

    @Override
    public void resetContainer() {
        resetContainer.resetContainer();
    }

    @Override
    public void setParentContainer(CheflingContainer container) {
        setParentContainer.setParentContainer(container);
    }

    @Override
    public void validateContainer() {
        validateContainer.validateContainer();
    }

}
