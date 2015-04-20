package com.cookingfox.chefling;

import com.cookingfox.chefling.command.*;
import com.cookingfox.chefling.exception.CommandInstantiationException;
import com.cookingfox.chefling.exception.ContainerException;

import java.lang.reflect.Constructor;
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
     * Stored command instances, where key is the command class.
     */
    protected final Map<Class, Object> commands = new HashMap<Class, Object>();

    /**
     * Stored instances, where key is the type and value is the instance.
     */
    protected final Map<Class, Object> instances = new HashMap<Class, Object>();

    /**
     * Type map, where key is the type that is requested and value is either a sub type that needs
     * to be created (through {@link #mapType(Class, Class)}) or a Factory.
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
        instances.remove(type);
        mappings.remove(type);
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

    @SuppressWarnings("unchecked")
    protected <T> T getCommand(Class<T> commandClass) throws CommandInstantiationException {
        T command = (T) commands.get(commandClass);

        // no stored command instance? create it
        if (command == null) {
            // use first constructor to create command instance
            Constructor constructor = commandClass.getDeclaredConstructors()[0];
            Object[] params = {this, instances, mappings};

            try {
                // create and store command instance
                command = (T) constructor.newInstance(params);
                commands.put(commandClass, command);
            } catch (Exception e) {
                throw new CommandInstantiationException(commandClass, e);
            }
        }

        return command;
    }

    /**
     * Initializes the container.
     */
    protected void initialize() {
        // map this instance to its class and interface
        instances.put(Container.class, this);
        instances.put(ContainerInterface.class, this);
    }

}
