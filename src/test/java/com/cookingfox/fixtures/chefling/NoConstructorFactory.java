package com.cookingfox.fixtures.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;

/**
 * Simple {@link CheflingFactory} implementation for {@link NoConstructor}.
 */
public class NoConstructorFactory implements CheflingFactory<NoConstructor> {
    @Override
    public NoConstructor createInstance(CheflingContainer container) {
        return new NoConstructor();
    }
}
