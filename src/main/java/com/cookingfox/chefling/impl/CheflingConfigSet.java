package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingConfigCollection;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link CheflingConfigCollection}, based on a {@link Set}. This class also
 * implements {@link CheflingConfig}, which means an instance can be added to a
 * {@link CheflingConfigCollection}.
 */
public class CheflingConfigSet implements CheflingConfig, CheflingConfigCollection {

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    /**
     * Collection of configuration objects.
     */
    protected final Set<CheflingConfig> configs = new LinkedHashSet<>();

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    /**
     * Create a new config set.
     *
     * @param configs Variable collection of configuration objects to add immediately.
     */
    public CheflingConfigSet(CheflingConfig... configs) {
        for (CheflingConfig config : configs) {
            addConfig(config);
        }
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public CheflingConfigCollection addConfig(CheflingConfig config) {
        if (containsConfig(config)) {
            throw new ContainerBuilderException("Config was already added");
        }

        configs.add(config);

        return this;
    }

    @Override
    public void apply(CheflingContainer container) {
        requireNonNull(container, "Container can not be null");

        for (CheflingConfig config : configs) {
            try {
                config.apply(container);
            } catch (Exception e) {
                throw new ContainerBuilderException("An error occurred during build container", e);
            }
        }
    }

    /**
     * Returns whether the provided config has already been added.
     *
     * @param config The config to check.
     * @return Whether the provided config has already been added.
     */
    @Override
    public boolean containsConfig(CheflingConfig config) {
        return configs.contains(requireNonNull(config, "Config can not be null"));
    }

    @Override
    public CheflingConfigCollection removeConfig(CheflingConfig config) {
        if (!containsConfig(config)) {
            throw new ContainerBuilderException("Can not remove config that is not added: " + config);
        }

        configs.remove(config);

        return this;
    }

}
