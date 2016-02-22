package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.Config;
import com.cookingfox.chefling.api.Container;
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
public class BuilderTest {

    private Chefling.Builder builder;

    //----------------------------------------------------------------------------------------------
    // SETUP
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        builder = new Chefling.Builder();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: add
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void add_should_throw_if_null() throws Exception {
        builder.add(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void add_should_throw_if_adding_same_instance_twice() throws Exception {
        final Config config = new NoopConfig();

        builder.add(config);
        builder.add(config);
    }

    @Test
    public void add_should_add_config() throws Exception {
        final Config first = new NoopConfig();
        final Config second = new NoopConfig();
        final Config third = new NoopConfig();

        builder.add(first);
        builder.add(second);
        builder.add(third);

        assertTrue(builder.configs.contains(first));
        assertTrue(builder.configs.contains(second));
        assertTrue(builder.configs.contains(third));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: build
    //----------------------------------------------------------------------------------------------

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_no_configs() throws Exception {
        builder.build();
    }

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_config_throws_container_exception() throws Exception {
        builder.add(new Config() {
            @Override
            public void apply(Container container) {
                throw new ContainerException("Example");
            }
        }).build();
    }

    @Test(expected = ContainerBuilderException.class)
    public void build_should_throw_if_config_throws_generic_error() throws Exception {
        builder.add(new Config() {
            @Override
            public void apply(Container container) {
                throw new RuntimeException("Example");
            }
        }).build();
    }

    @Test
    public void build_should_apply_config() throws Exception {
        Container container = builder.add(new Config() {
            @Override
            public void apply(Container container) {
                container.mapInstance(NoConstructor.class, new NoConstructor());
            }
        }).build();

        assertTrue(container.has(NoConstructor.class));
    }

    @Test
    public void build_should_apply_configs_in_sequence() throws Exception {
        final List<Config> testList = new LinkedList<>();
        final Config first = new AddToListConfig(testList);
        final Config second = new AddToListConfig(testList);
        final Config third = new AddToListConfig(testList);

        builder.add(first).add(second).add(third).build();

        final Iterator<Config> iterator = testList.iterator();

        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
        assertSame(third, iterator.next());
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    private static class NoopConfig implements Config {
        @Override
        public void apply(Container container) {
            // no-op
        }
    }

    private static class AddToListConfig implements Config {
        final List<Config> list;

        public AddToListConfig(List<Config> list) {
            this.list = list;
        }

        @Override
        public void apply(Container container) {
            list.add(this);
        }
    }

}
