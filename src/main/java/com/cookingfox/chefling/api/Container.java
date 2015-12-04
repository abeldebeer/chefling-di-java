package com.cookingfox.chefling.api;

import com.cookingfox.chefling.api.command.*;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
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
        ResetCommand {
}
