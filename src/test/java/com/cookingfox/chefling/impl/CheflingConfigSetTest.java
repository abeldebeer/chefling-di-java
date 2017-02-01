package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link CheflingConfigSet}.
 */
public class CheflingConfigSetTest {

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
