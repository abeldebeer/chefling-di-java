package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.command.*;

/**
 * Chefling Container interface.
 */
public interface Container extends

        AddChildCommand,
        CreateCommand,
        GetCommand,
        HasCommand,
        MapFactoryCommand,
        MapInstanceCommand,
        MapTypeCommand,
        RemoveCommand,
        ResetCommand,
        SetParentCommand {

}
