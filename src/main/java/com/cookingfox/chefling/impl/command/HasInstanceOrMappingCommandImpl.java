package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.HasInstanceOrMappingCommand;

/**
 * @see HasInstanceOrMappingCommand
 */
public class HasInstanceOrMappingCommandImpl extends AbstractCommand implements HasInstanceOrMappingCommand {

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
    public boolean hasInstanceOrMapping(Class type) {
        return findOneWithInstanceOrMapping(_container, type) != null;
    }

}
