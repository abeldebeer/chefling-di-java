package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.impl.helper.Matcher;

/**
 * @see com.cookingfox.chefling.api.command.HasCommand
 */
class HasCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.HasCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public HasCommand(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean has(final Class type) {
        Matcher matcher = HasMappingMatcher.get(type);

        return matcher.matches(_container) || findOne(_container, matcher) != null;
    }

}
