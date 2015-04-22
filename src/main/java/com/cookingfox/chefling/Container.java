package com.cookingfox.chefling;

import com.cookingfox.chefling.command.*;
import com.cookingfox.chefling.exception.ContainerException;

import java.util.HashMap;
import java.util.Map;

/**
 * @see com.cookingfox.chefling.ContainerInterface
 */
public class Container implements ContainerInterface {

    //----------------------------------------------------------------------------------------------
    // PROTECTED PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Stores operation commands, where the key is the command class and the value is the instance.
     */
    protected final Map<Class, Object> commands = new HashMap<Class, Object>();

    /**
     * Stores instances, either created by the container or mapped using
     * {@link #mapInstance(Class, Object)}, where the key is the type and the value is the instance.
     */
    protected final Map<Class, Object> instances = new HashMap<Class, Object>();

    /**
     * Stores type mappings, where the key is the type and the value is the mapping provided by the
     * {@link #mapFactory(Class, Factory)} and {@link #mapType(Class, Class)} methods.
     */
    protected final Map<Class, Object> mappings = new HashMap<Class, Object>();

    /**
     * Convenience singleton for apps using a process-wide Container instance.
     */
    protected static volatile Container defaultInstance;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Default constructor: initializes the container.
     */
    public Container() {
        initialize();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @see com.cookingfox.chefling.ContainerInterface#create(Class)
     */
    @Override
    public <T> T create(Class<T> type) throws ContainerException {
        return getCommand(CreateCommand.class).create(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#get(Class)
     */
    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        return getCommand(GetCommand.class).get(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#has(Class)
     */
    @Override
    public boolean has(Class type) {
        return instances.containsKey(type) || mappings.containsKey(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#mapFactory(Class, Factory)
     */
    @Override
    public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {
        getCommand(MapFactoryCommand.class).mapFactory(type, factory);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#mapInstance(Class, Object)
     */
    @Override
    public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {
        getCommand(MapInstanceCommand.class).mapInstance(type, instance);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#mapType(Class, Class)
     */
    @Override
    public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {
        getCommand(MapTypeCommand.class).mapType(type, subType);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#remove(Class)
     */
    @Override
    public void remove(Class type) {
        // call destroy method for life cycle objects
        lifeCycleDestroy(instances.get(type));

        instances.remove(type);
        mappings.remove(type);
    }

    /**
     * @see com.cookingfox.chefling.ContainerInterface#reset()
     */
    @Override
    public void reset() {
        // call destroy method for life cycle objects
        for (Map.Entry<Class, Object> entry : instances.entrySet()) {
            lifeCycleDestroy(entry.getValue());
        }

        commands.clear();
        instances.clear();
        mappings.clear();

        initialize();
    }

    /**
     * Convenience singleton for apps using a process-wide Container instance.
     *
     * @return Default Container instance.
     */
    public static Container getDefault() {
        if (defaultInstance == null) {
            synchronized (Container.class) {
                if (defaultInstance == null) {
                    defaultInstance = new Container();
                }
            }
        }

        return defaultInstance;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Get one of the operation commands by its type.
     *
     * @param commandClass Class of the command.
     * @param <T>          Ensures the instance is cast to the expected type.
     * @return A command instance.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getCommand(Class<T> commandClass) {
        return (T) commands.get(commandClass);
    }

    /**
     * Initializes the container.
     */
    protected void initialize() {
        // create operation commands
        commands.put(CreateCommand.class, new CreateCommand(this, instances, mappings));
        commands.put(GetCommand.class, new GetCommand(this, instances, mappings));
        commands.put(MapFactoryCommand.class, new MapFactoryCommand(this, instances, mappings));
        commands.put(MapInstanceCommand.class, new MapInstanceCommand(this, instances, mappings));
        commands.put(MapTypeCommand.class, new MapTypeCommand(this, instances, mappings));

        // map this instance to its class and interface
        instances.put(Container.class, this);
        instances.put(ContainerInterface.class, this);
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

}
