package com.cookingfox.chefling.impl.helper;

import com.cookingfox.chefling.impl.command.CommandContainer;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public interface Matcher {
    boolean matches(CommandContainer container);
}
