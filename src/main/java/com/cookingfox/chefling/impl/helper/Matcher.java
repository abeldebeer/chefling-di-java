package com.cookingfox.chefling.impl.helper;

import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Interface for matching a collection of {@link CommandContainer} instances. Can be used to find
 * one element in a composite tree of containers.
 */
public interface Matcher {

    /**
     * Specify a condition to match the container instance.
     *
     * @param container Current container instance.
     * @return Result to indicate whether the container matches the condition.
     */
    boolean matches(CommandContainer container);

}
