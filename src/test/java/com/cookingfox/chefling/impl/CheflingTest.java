package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.api.CheflingBuilder;
import com.cookingfox.chefling.api.CheflingConfig;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.impl.command.CommandContainer;
import com.cookingfox.chefling.impl.command.CommandContainerBuilder;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Chefling}.
 */
public class CheflingTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_be_private() throws Exception {
        Constructor<Chefling> constructor = Chefling.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: builder
    //----------------------------------------------------------------------------------------------

    @Test
    public void builder_should_return_new_builder() throws Exception {
        assertTrue(Chefling.createBuilder() instanceof CommandContainerBuilder);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: createContainer
    //----------------------------------------------------------------------------------------------

    @Test
    public void createContainer_should_create_new_command_container() throws Exception {
        assertTrue(Chefling.createContainer() instanceof CommandContainer);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: validateBuilderAndContainer
    //----------------------------------------------------------------------------------------------

    @Test
    public void validateContainer_should_perform_all_validation_steps() throws Exception {
        final Collection<ContainerEventsEnum> expectedEvents = new LinkedList<>();
        expectedEvents.add(ContainerEventsEnum.PRE_BUILDER_APPLY);
        expectedEvents.add(ContainerEventsEnum.APPLY_CONFIG);
        expectedEvents.add(ContainerEventsEnum.POST_BUILDER_APPLY);
        expectedEvents.add(ContainerEventsEnum.INITIALIZE);
        expectedEvents.add(ContainerEventsEnum.PRE_CONTAINER_DISPOSE);
        expectedEvents.add(ContainerEventsEnum.DISPOSE);
        expectedEvents.add(ContainerEventsEnum.POST_CONTAINER_DISPOSE);

        final Collection<ContainerEventsEnum> actualEvents = new LinkedList<>();

        CheflingBuilder builder = Chefling.createBuilder()
                .addConfig(new CheflingConfig() {
                    @Override
                    public void apply(CheflingContainer container) {
                        actualEvents.add(ContainerEventsEnum.APPLY_CONFIG);

                        // for logging events
                        container.mapInstance(ContainerEventsLifecycle.class, new ContainerEventsLifecycle(actualEvents));

                        // fixture mappings
                        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
                        container.mapFactory(NoConstructor.class, new GenericInstanceFactory<>(new NoConstructor()));
                    }
                })
                .addContainerListener(new CheflingContainerListener() {
                    @Override
                    public void preBuilderApply(CheflingContainer container) {
                        actualEvents.add(ContainerEventsEnum.PRE_BUILDER_APPLY);
                    }

                    @Override
                    public void postBuilderApply(CheflingContainer container) {
                        actualEvents.add(ContainerEventsEnum.POST_BUILDER_APPLY);
                    }

                    @Override
                    public void preContainerDispose(CheflingContainer container) {
                        actualEvents.add(ContainerEventsEnum.PRE_CONTAINER_DISPOSE);
                    }

                    @Override
                    public void postContainerDispose(CheflingContainer container) {
                        actualEvents.add(ContainerEventsEnum.POST_CONTAINER_DISPOSE);
                    }
                });

        Chefling.validateBuilderAndContainer(builder);

        assertEquals(expectedEvents, actualEvents);
    }

}
