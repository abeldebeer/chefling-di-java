package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;

import java.util.List;

/**
 * Implementation of {@link CheflingConfig} that adds the config instance to a provided list when
 * the {@link #apply(CheflingContainer)} method is called.
 */
public class AddToListConfig implements CheflingConfig {

    final List<CheflingConfig> list;

    public AddToListConfig(List<CheflingConfig> list) {
        this.list = list;
    }

    @Override
    public void apply(CheflingContainer container) {
        list.add(this);
    }

}
