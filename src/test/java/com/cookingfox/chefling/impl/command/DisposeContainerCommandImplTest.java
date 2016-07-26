package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingContainerListener;
import com.cookingfox.chefling.impl.Chefling;
import com.cookingfox.chefling.impl.helper.DefaultCheflingContainerListener;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DisposeContainerCommandImpl}.
 */
public class DisposeContainerCommandImplTest extends AbstractTest {

    @Test
    public void should_remove_mappings_and_instances_and_listeners() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.getInstance(NoMethodInterface.class);

        container.disposeContainer();

        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));

        assertTrue(container.containerListeners.isEmpty());
        assertTrue(container.instances.isEmpty());
        assertTrue(container.mappings.isEmpty());
    }

    @Test
    public void should_remove_children_and_parent() throws Exception {
        CommandContainer childContainer = (CommandContainer) Chefling.createContainer();
        CommandContainer parentContainer = (CommandContainer) Chefling.createContainer();

        container.addChildContainer(childContainer);
        container.setParentContainer(parentContainer);

        container.disposeContainer();

        assertTrue(container.children.isEmpty());
        assertNull(container.parent);

        assertTrue(childContainer.children.isEmpty());
        assertNull(childContainer.parent);

        assertTrue(parentContainer.children.isEmpty());
        assertNull(parentContainer.parent);
    }

    @Test
    public void should_call_container_listener_methods() throws Exception {
        final AtomicInteger preContainerDisposeCalls = new AtomicInteger(0);
        final AtomicInteger postContainerDisposeCalls = new AtomicInteger(0);

        container.addContainerListeners(Collections.<CheflingContainerListener>singleton(
                new DefaultCheflingContainerListener() {
                    @Override
                    public void preContainerDispose(CheflingContainer container) {
                        preContainerDisposeCalls.getAndIncrement();
                    }

                    @Override
                    public void postContainerDispose(CheflingContainer container) {
                        postContainerDisposeCalls.getAndIncrement();
                    }
                }
        ));

        container.disposeContainer();

        assertEquals(1, preContainerDisposeCalls.get());
        assertEquals(1, postContainerDisposeCalls.get());
    }

    @Test
    public void should_clear_container_listeners() throws Exception {
        CheflingContainerListener listener = new DefaultCheflingContainerListener();

        container.addContainerListeners(Collections.singleton(listener));

        assertTrue(container.containerListeners.contains(listener));

        container.disposeContainer();

        assertFalse(container.containerListeners.contains(listener));
    }

}
