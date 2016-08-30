package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.api.exception.ContainerBuilderException;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.impl.Chefling;
import com.cookingfox.chefling.impl.helper.DefaultCheflingContainerListener;
import com.cookingfox.fixtures.chefling.AddToListConfig;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoopConfig;
import com.cookingfox.fixtures.chefling.NoopContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CommandContainerBuilder}.
 */
public class CommandContainerBuilderTest {

    private CommandContainerBuilder builder;

    //----------------------------------------------------------------------------------------------
    // SETUP
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        builder = new CommandContainerBuilder();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addConfig
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addConfig_should_throw_if_null() throws Exception {
        builder.addConfig(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void addConfig_should_throw_if_adding_same_instance_twice() throws Exception {
        CheflingConfig config = new NoopConfig();

        builder.addConfig(config);
        builder.addConfig(config);
    }

    @Test
    public void addConfig_should_add_config() throws Exception {
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
    // TESTS: addContainerListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addContainerListener_should_throw_if_null() throws Exception {
        builder.addContainerListener(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void addContainerListener_should_throw_if_same_added_twice() throws Exception {
        CheflingContainerListener listener = new DefaultCheflingContainerListener();

        builder.addContainerListener(listener);
        builder.addContainerListener(listener);
    }

    @Test
    public void addContainerListener_should_return_builder() throws Exception {
        CheflingBuilder result = builder.addContainerListener(new DefaultCheflingContainerListener());

        assertSame(builder, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: applyToContainer
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void applyToContainer_should_throw_if_container_null() throws Exception {
        builder.applyToContainer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyToContainer_should_throw_if_not_command_container() throws Exception {
        builder.applyToContainer(new NoopContainer());
    }

    @Test
    public void applyToContainer_should_return_provided_instance() throws Exception {
        CheflingContainer container = Chefling.createContainer();

        builder.addConfig(new NoopConfig());

        CheflingContainer returned = builder.applyToContainer(container);

        assertSame(container, returned);
    }

    @Test
    public void applyToContainer_should_not_throw_if_no_configs() throws Exception {
        builder.applyToContainer(Chefling.createContainer());
    }

    @Test(expected = ContainerBuilderException.class)
    public void applyToContainer_should_throw_if_config_throws_container_exception() throws Exception {
        builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                throw new ContainerException("Example");
            }
        }).applyToContainer(Chefling.createContainer());
    }

    @Test(expected = ContainerBuilderException.class)
    public void applyToContainer_should_throw_if_config_throws_generic_error() throws Exception {
        builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                throw new RuntimeException("Example");
            }
        }).applyToContainer(Chefling.createContainer());
    }

    @Test
    public void applyToContainer_should_apply_config() throws Exception {
        CheflingContainer container = builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                container.mapInstance(NoConstructor.class, new NoConstructor());
            }
        }).applyToContainer(Chefling.createContainer());

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void applyToContainer_should_apply_configs_in_sequence() throws Exception {
        List<CheflingConfig> testList = new LinkedList<>();
        CheflingConfig first = new AddToListConfig(testList);
        CheflingConfig second = new AddToListConfig(testList);
        CheflingConfig third = new AddToListConfig(testList);

        builder.addConfig(first)
                .addConfig(second)
                .addConfig(third)
                .applyToContainer(Chefling.createContainer());

        Iterator<CheflingConfig> iterator = testList.iterator();

        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
        assertSame(third, iterator.next());
    }

    @Test
    public void applyToContainer_should_call_container_listener_builder_methods() throws Exception {
        final AtomicInteger preBuilderApplyCalls = new AtomicInteger(0);
        final AtomicInteger postBuilderApplyCalls = new AtomicInteger(0);

        builder.addContainerListener(new DefaultCheflingContainerListener() {
            @Override
            public void preBuilderApply(CheflingContainer container) {
                preBuilderApplyCalls.getAndIncrement();
            }

            @Override
            public void postBuilderApply(CheflingContainer container) {
                postBuilderApplyCalls.getAndIncrement();
            }
        });

        builder.applyToContainer(Chefling.createContainer());

        assertEquals(1, preBuilderApplyCalls.get());
        assertEquals(1, postBuilderApplyCalls.get());
    }

    @Test
    public void applyToContainer_should_add_listener_to_container() throws Exception {
        CheflingContainerListener listener = new DefaultCheflingContainerListener();
        CommandContainer container = new CommandContainer();

        builder.addContainerListener(listener);
        builder.applyToContainer(container);

        assertTrue(container.containerListeners.contains(listener));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: buildContainer
    //----------------------------------------------------------------------------------------------

    @Test
    public void buildContainer_should_create_container_and_apply_config() throws Exception {
        CheflingContainer container = builder.addConfig(new CheflingConfig() {
            @Override
            public void apply(CheflingContainer container) {
                container.mapInstance(NoConstructor.class, new NoConstructor());
            }
        }).buildContainer();

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeConfig
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeConfig_should_throw_if_config_null() throws Exception {
        builder.removeConfig(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void removeConfig_should_throw_if_not_added() throws Exception {
        builder.removeConfig(new NoopConfig());
    }

    @Test
    public void removeConfig_should_remove_config() throws Exception {
        CheflingConfig config = new NoopConfig();

        builder.addConfig(config);

        assertTrue(builder.configs.contains(config));

        builder.removeConfig(config);

        assertFalse(builder.configs.contains(config));
    }

    @Test
    public void removeConfig_should_return_builder() throws Exception {
        CheflingConfig config = new NoopConfig();
        builder.addConfig(config);

        CheflingBuilder returned = this.builder.removeConfig(config);

        assertSame(builder, returned);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: removeContainerListener
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void removeContainerListener_should_throw_if_null() throws Exception {
        builder.removeContainerListener(null);
    }

    @Test(expected = ContainerBuilderException.class)
    public void removeContainerListener_should_throw_if_not_added() throws Exception {
        builder.removeContainerListener(new DefaultCheflingContainerListener());
    }

    @Test
    public void removeContainerListener_should_return_builder() throws Exception {
        CheflingContainerListener listener = new DefaultCheflingContainerListener();

        builder.addContainerListener(listener);

        CheflingBuilder result = builder.removeContainerListener(listener);

        assertSame(builder, result);
    }

}
