package com.cookingfox.chefling.api.exception;

/**
 * Thrown when a child Container's configuration (instances / mappings) intersects with the
 * Container it is being added to.
 */
public class ChildConfigurationConflictException extends ContainerException {

    public ChildConfigurationConflictException(Class type) {
        super(String.format("The child Container you are trying to add has a mapping or instance " +
                "for a type that is also present in the current Container (%s)", type.getName()));
    }

}
