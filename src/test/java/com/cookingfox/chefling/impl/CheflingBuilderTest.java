package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Chefling.Builder}.
 */
public class CheflingBuilderTest {

    private Chefling.Builder builder;

    //----------------------------------------------------------------------------------------------
    // SETUP
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        builder = new Chefling.Builder();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addConfig
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void add_should_throw_if_null() throws Exception {
        builder.addConfig(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void add_should_throw_if_adding_same_instance_twice() throws Exception {
        CheflingConfig config = new NoopConfig();

        builder.addConfig(config);
        builder.addConfig(config);
    }

    @Test
    public void add_should_add_config() throws Exception {
        CheflingConfig first = new NoopConfig();
        CheflingConfig second = new NoopConfig();
        CheflingConfig third = new NoopConfig();

        builder.addConfig(first);
        builder.addConfig(second);
        builder.addConfig(third);

        assertTrue(builder.configs.contains(first));
        assertTrue(builder.configs.contains(second));
        assertTrue(builder.configs.contains(third));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: buildContainer
    //----------------------------------------------------------------------------------------------

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_no_configs() throws Exception {
        builder.buildContainer();
    }

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_config_throws_container_exception() throws Exception {
        builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                throw new ContainerException("Example");
            }
        }).buildContainer();
    }

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_config_throws_generic_error() throws Exception {
        builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                throw new RuntimeException("Example");
            }
        }).buildContainer();
    }

    @Test
    public void build_should_apply_config() throws Exception {
        CheflingContainer container = builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                container.mapInstance(NoConstructor.class, new NoConstructor());
            }
        }).buildContainer();

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void build_should_apply_configs_in_sequence() throws Exception {
        List<CheflingConfig> testList = new LinkedList<>();
        CheflingConfig first = new AddToListConfig(testList);
        CheflingConfig second = new AddToListConfig(testList);
        CheflingConfig third = new AddToListConfig(testList);

        builder.addConfig(first).addConfig(second).addConfig(third).buildContainer();

        Iterator<CheflingConfig> iterator = testList.iterator();

        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
        assertSame(third, iterator.next());
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    private static class NoopConfig implements CheflingConfig {
        @Override
        public void apply(CheflingContainer container) {
            // no-op
        }
    }

    private static class AddToListConfig implements CheflingConfig {
        List<CheflingConfig> list;

        public AddToListConfig(List<CheflingConfig> list) {
            this.list = list;
        }

        @Override
        public void apply(CheflingContainer container) {
            list.add(this);
        }
    }

}
