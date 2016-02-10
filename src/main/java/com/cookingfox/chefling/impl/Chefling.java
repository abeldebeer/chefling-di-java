package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.Config;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.chefling.impl.command.CommandContainer;

import java.util.LinkedList;
import java.util.Objects;

/**
 * Helper class for managing Chefling containers.
 */
public class Chefling {

    /**
     * Creates a new instance of the default Chefling {@link Container} implementation.
     */
    public static Container createContainer() {
        return new CommandContainer();
    }

    public static class Builder implements com.cookingfox.chefling.api.Builder {

        final protected LinkedList<Config> configs = new LinkedList<>();

        public Builder add(final Config config) {
            Objects.requireNonNull(config, "Config can not be null");

            if (configs.contains(config)) {
                throw new ContainerBuilderException("Config was already added");
            }

            configs.add(config);

            return this;
        }

        public Container build() {
            if (configs.isEmpty()) {
                throw new ContainerBuilderException("Add configs first");
            }

            final Container container = createContainer();

            for (Config config : configs) {
                try {
                    config.apply(container);
                } catch (Exception e) {
                    throw new ContainerBuilderException("An error occurred during build", e);
                }
            }

            return container;
        }

    }

}
