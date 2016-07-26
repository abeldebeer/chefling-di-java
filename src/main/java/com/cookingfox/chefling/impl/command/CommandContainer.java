package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.command.*;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
    protected final Set<CommandContainer> children = new LinkedHashSet<>();

    /**
     * A collection of container listeners, so that they can be notified of events in the container
     * lifecycle.
     */
    protected final Set<CheflingContainerListener> containerListeners = new LinkedHashSet<>();

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

    protected AddChildContainerCommand addChildContainer = new AddChildContainerCommandImpl(this);
    protected CreateChildContainerCommand createChildContainer = new CreateChildContainerCommandImpl(this);
    protected CreateInstanceCommand createInstance = new CreateInstanceCommandImpl(this);
    protected DisposeContainerCommand disposeContainer = new DisposeContainerCommandImpl(this);
    protected GetInstanceCommand getInstance = new GetInstanceCommandImpl(this);
    protected HasInstanceOrMappingCommand hasInstanceOrMapping = new HasInstanceOrMappingCommandImpl(this);
    protected MapFactoryCommand mapFactory = new MapFactoryCommandImpl(this);
    protected MapInstanceCommand mapInstance = new MapInstanceCommandImpl(this);
    protected MapTypeCommand mapType = new MapTypeCommandImpl(this);
    protected RemoveInstanceAndMappingCommand removeInstanceAndMapping = new RemoveInstanceAndMappingCommandImpl(this);
    protected SetParentContainerCommand setParentContainer = new SetParentContainerCommandImpl(this);
    protected ValidateContainerCommand validateContainer = new ValidateContainerCommandImpl(this);

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public CommandContainer() {
        initializeContainer();
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
        return createChildContainer.createChildContainer();
    }

    @Override
    public void disposeContainer() {
        disposeContainer.disposeContainer();
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
    public void setParentContainer(CheflingContainer container) {
        setParentContainer.setParentContainer(container);
    }

    @Override
    public void validateContainer() {
        validateContainer.validateContainer();
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Add a collection of container listeners.
     *
     * @param containerListeners The collection of listeners to add.
     */
    protected void addContainerListeners(Set<CheflingContainerListener> containerListeners) {
        this.containerListeners.addAll(containerListeners);
    }

    /**
     * Initialize the container.
     */
    protected void initializeContainer() {
        // set references to current container instance
        instances.put(CheflingContainer.class, this);
        instances.put(CommandContainer.class, this);
    }

}
