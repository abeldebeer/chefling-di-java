package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.chefling.impl.command.CommandContainer;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Helper class for managing Chefling containers.
 */
public final class Chefling {

    private Chefling() {
        // should not be instantiated
    }

    /**
     * Creates a new Chefling Builder.
     */
    public static CheflingBuilder builder() {
        return new Builder();
    }

    /**
     * Creates a new instance of the default Chefling {@link CheflingContainer} implementation.
     */
    public static CheflingContainer createContainer() {
        return new CommandContainer();
    }

    /**
     * Chefling Builder implementation.
     */
    public static class Builder implements CheflingBuilder {

        /**
         * List of added configurations.
         */
        protected final Set<CheflingConfig> configs = new LinkedHashSet<>();

        @Override
        public CheflingBuilder addConfig(CheflingConfig config) {
            if (configs.contains(requireNonNull(config, "Config can not be null"))) {
                throw new ContainerBuilderException("Config was already added");
            }

            configs.add(config);

            return this;
        }

        @Override
        public CheflingContainer applyToContainer(CheflingContainer container) {
            requireNonNull(container, "Container can not be null");

            if (configs.isEmpty()) {
                throw new ContainerBuilderException("Add configs first");
            }

            for (CheflingConfig config : configs) {
                try {
                    config.apply(container);
                } catch (Exception e) {
                    throw new ContainerBuilderException("An error occurred during build container", e);
                }
            }

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

    }

}
