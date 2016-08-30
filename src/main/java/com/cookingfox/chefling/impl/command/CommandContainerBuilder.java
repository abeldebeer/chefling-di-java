package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link CheflingBuilder} that is used to configure and create an instance of the
 * {@link CommandContainer}.
 */
public class CommandContainerBuilder implements CheflingBuilder {

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Collection of configuration objects.
     */
    protected final Set<CheflingConfig> configs = new LinkedHashSet<>();

    /**
     * Collection of container listeners.
     */
    protected final Set<CheflingContainerListener> containerListeners = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // PUBLIC STATIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of the default Chefling {@link CheflingContainer} implementation.
     *
     * @return A new container instance.
     */
    public static CheflingContainer createContainer() {
        return new CommandContainer();
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public CheflingBuilder addConfig(CheflingConfig config) {
        if (configs.contains(requireNonNull(config, "Config can not be null"))) {
            throw new ContainerBuilderException("Config was already added");
        }

        configs.add(config);

        return this;
    }

    @Override
    public CheflingBuilder addContainerListener(CheflingContainerListener listener) {
        if (containerListeners.contains(requireNonNull(listener, "Listener can not be null"))) {
            throw new ContainerBuilderException("Container listener was already added");
        }

        containerListeners.add(listener);

        return this;
    }

    @Override
    public CheflingContainer applyToContainer(CheflingContainer container) {
        requireNonNull(container, "Container can not be null");

        if (!(container instanceof CommandContainer)) {
            throw new IllegalArgumentException("Expected a `CommandContainer` instance");
        }

        // container listener: pre builder apply
        for (CheflingContainerListener containerListener : containerListeners) {
            containerListener.preBuilderApply(container);
        }

        // apply all configs
        for (CheflingConfig config : configs) {
            try {
                config.apply(container);
            } catch (Exception e) {
                throw new ContainerBuilderException("An error occurred during build container", e);
            }
        }

        // container listener: post builder apply
        for (CheflingContainerListener containerListener : containerListeners) {
            containerListener.postBuilderApply(container);
        }

        // add container listeners so they can be used to during the dispose phase
        ((CommandContainer) container).addContainerListeners(containerListeners);

        return container;
    }

    @Override
    public CheflingContainer buildContainer() {
        return applyToContainer(createContainer());
    }

    @Override
    public CheflingBuilder removeConfig(CheflingConfig config) {
        if (!configs.contains(requireNonNull(config, "Config can not be null"))) {
            throw new ContainerBuilderException("Can not remove config that is not added: " + config);
        }

        configs.remove(config);

        return this;
    }

    @Override
    public CheflingBuilder removeContainerListener(CheflingContainerListener listener) {
        if (!containerListeners.contains(requireNonNull(listener, "Listener can not be null"))) {
            throw new ContainerBuilderException("Can not remove container listener that is not added: " + listener);
        }

        containerListeners.remove(listener);

        return this;
    }

}
