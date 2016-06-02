package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a container's configuration (instances / mappings) intersects with the container it
 * is being added to.
 */
public class ConfigurationConflictException extends ContainerException {

    public ConfigurationConflictException(Class type) {
        super(String.format("The CheflingContainer you are trying to add / set has a mapping or instance " +
                "for a type that is also present in the current CheflingContainer (%s)", type.getName()));
    }

}
