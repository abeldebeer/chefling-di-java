package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.HasInstanceOrMappingCommand;
import com.cookingfox.chefling.impl.helper.CommandContainerMatcher;

/**
 * @see HasInstanceOrMappingCommand
 */
class HasInstanceOrMappingCommandImpl extends AbstractCommand implements HasInstanceOrMappingCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public HasInstanceOrMappingCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean hasInstanceOrMapping(final Class type) {
        CommandContainerMatcher matcher = HasMappingMatcher.get(type);

        return matcher.matches(_container) || findOne(_container, matcher) != null;
    }

}
