package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.HasCommand;
import com.cookingfox.chefling.impl.helper.Matcher;

/**
 * @see HasCommand
 */
class HasCommandImpl extends AbstractCommand implements HasCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public HasCommandImpl(CommandContainer container) {
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
