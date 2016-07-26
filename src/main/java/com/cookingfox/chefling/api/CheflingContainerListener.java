package com.cookingfox.chefling.api;

/**
 * Hooks for a {@link CheflingContainer}.
 */
public interface CheflingContainerListener {

    /**
     * Called before the {@link CheflingBuilder} applies all added {@link CheflingConfig} instances.
     * At this point the config mappings are not yet available.
     *
     * @param container The current container instance.
     */
    void preBuilderApply(CheflingContainer container);

    /**
     * Called after the {@link CheflingBuilder} applied all added {@link CheflingConfig} instances.
     * At this point all config mappings are available.
     *
     * @param container The current container instance.
     */
    void postBuilderApply(CheflingContainer container);

    /**
     * Called before the container disposes all stored instances and clears all mappings. At this
     * point all instances and mappings are still available.
     *
     * @param container The current container instance.
     * @see CheflingLifecycle#dispose()
     */
    void preContainerDispose(CheflingContainer container);

    /**
     * Called after the container disposed all stored instances and cleared all mappings. At this
     * point all instances and mappings are no longer available.
     *
     * @param container The current container instance.
     * @see CheflingLifecycle#dispose()
     */
    void postContainerDispose(CheflingContainer container);

}
