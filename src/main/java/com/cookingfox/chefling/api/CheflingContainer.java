package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.command.*;

/**
 * A dependency injection container that maps types (classes, interfaces) to instances. It resolves
 * a class's full dependency tree using constructor injection.
 */
public interface CheflingContainer extends

        AddChildContainerCommand,
        CreateChildContainerCommand,
        CreateInstanceCommand,
        DisposeContainerCommand,
        GetInstanceCommand,
        HasInstanceOrMappingCommand,
        MapFactoryCommand,
        MapInstanceCommand,
        MapTypeCommand,
        RemoveInstanceAndMappingCommand,
        SetParentContainerCommand,
        ValidateContainerCommand {

}
