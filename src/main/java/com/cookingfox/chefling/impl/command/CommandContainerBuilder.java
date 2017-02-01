package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.chefling.impl.CheflingConfigSet;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link CheflingBuilder} that is used to configure and create an instance of the
 * {@link CommandContainer}.
 */
public class CommandContainerBuilder extends CheflingConfigSet implements CheflingBuilder {

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

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
        return (CheflingBuilder) super.addConfig(config);
    }

    @Override
    public CheflingBuilder addContainerListener(CheflingContainerListener listener) {
        if (containerListeners.contains(requireNonNull(listener, "Listener can not be null"))) {
            throw new ContainerBuilderException("Container listener was already added");
        }

        containerListeners.add(listener);

        return this;
    }

    /**
     * WARNING: this method is not meant to be called directly! Instead, call
     * {@link #applyToContainer(CheflingContainer)}.
     *
     * @param container The container instance that is being configured.
     * @throws UnsupportedOperationException when this method is called directly.
     */
    @Override
    public void apply(CheflingContainer container) {
        throw new UnsupportedOperationException("Do not call this method directly, instead, call " +
                "`applyToContainer(CheflingContainer)`");
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

        // apply all configs to container
        super.apply(container);

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
    public boolean containsConfig(CheflingConfig config) {
        return super.containsConfig(config);
    }

    @Override
    public CheflingBuilder removeConfig(CheflingConfig config) {
        return (CheflingBuilder) super.removeConfig(config);
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
