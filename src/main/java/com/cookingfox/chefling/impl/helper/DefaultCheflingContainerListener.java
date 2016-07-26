package com.cookingfox.chefling.impl.helper;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;

/**
 * Implementation of {@link CheflingContainerListener} with no-op methods.
 */
public class DefaultCheflingContainerListener implements CheflingContainerListener {

    @Override
    public void preBuilderApply(CheflingContainer container) {
        // override in subclass
    }

    @Override
    public void postBuilderApply(CheflingContainer container) {
        // override in subclass
    }

    @Override
    public void preContainerDispose(CheflingContainer container) {
        // override in subclass
    }

    @Override
    public void postContainerDispose(CheflingContainer container) {
        // override in subclass
    }

}
