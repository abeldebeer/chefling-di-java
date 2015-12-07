package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.impl.helper.Matcher;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class HasCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.HasCommand {
    public HasCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public boolean has(final Class type) {
        Matcher matcher = HasMappingMatcher.get(type);

        return matcher.matches(_container) || find(_container, matcher) != null;
    }

}
