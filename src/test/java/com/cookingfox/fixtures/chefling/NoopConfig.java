package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;

/**
 * No-operation implementation of CheflingConfig.
 */
public class NoopConfig implements CheflingConfig {

    @Override
    public void apply(CheflingContainer container) {
        // no-op
    }

}
