package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.command.*;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface Container extends

        AddChildCommand,
        CreateChildCommand,
        CreateCommand,
        GetCommand,
        HasCommand,
        MapFactoryCommand,
        MapInstanceCommand,
        MapTypeCommand,
        RemoveCommand,
        ResetCommand,
        SetParentCommand,
        TestCommand {

}
