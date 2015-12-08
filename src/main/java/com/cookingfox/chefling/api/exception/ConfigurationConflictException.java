package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a Container's configuration (instances / mappings) intersects with the Container it
 * is being added to.
 */
public class ConfigurationConflictException extends ContainerException {

    public ConfigurationConflictException(Class type) {
        super(String.format("The Container you are trying to add / set has a mapping or instance " +
                "for a type that is also present in the current Container (%s)", type.getName()));
    }

}
