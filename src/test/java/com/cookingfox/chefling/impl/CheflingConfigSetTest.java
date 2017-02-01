package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.fixtures.chefling.NoopConfig;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link CheflingConfigSet}.
 */
public class CheflingConfigSetTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void constructor_should_throw_if_config_null() throws Exception {
        new CheflingConfigSet(null, null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void constructor_should_throw_if_same_config_multiple_times() throws Exception {
        CheflingConfig config = new NoopConfig();

        new CheflingConfigSet(config, config);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: apply
    //----------------------------------------------------------------------------------------------

    @Test
    public void apply_should_apply_configs_from_set() throws Exception {
        final int numConfigs = 5;
        final CheflingConfig[] configs = new CheflingConfig[numConfigs];
        final Set<Integer> expected = new LinkedHashSet<>();
        final Set<Integer> actual = new LinkedHashSet<>();

        for (int i = 0; i < numConfigs; i++) {
            final int current = i;

            expected.add(current);

            configs[current] = new CheflingConfig() {
                @Override
                public void apply(CheflingContainer container) {
                    actual.add(current);
                }
            };
        }

        CheflingConfigSet root = new CheflingConfigSet();
        root.addConfig(new CheflingConfigSet(configs));
        root.apply(Chefling.createContainer());

        assertEquals(expected, actual);
    }

}
