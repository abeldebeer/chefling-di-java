package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ValidateContainerCommandImpl}.
 */
public class ValidateContainerCommandImplTest extends AbstractTest {

    @Test
    public void should_resolve_all_current_mappings() throws Exception {
        final AtomicBoolean factoryCalled = new AtomicBoolean(false);

        container.mapFactory(NoConstructor.class, new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                factoryCalled.set(true);
                return new NoConstructor();
            }
        });

        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertFalse(container.hasInstanceOrMapping(NoMethodImplementation.class));

        container.validateContainer();

        assertTrue(factoryCalled.get());
        assertTrue(container.hasInstanceOrMapping(NoMethodImplementation.class));
    }

    @Test
    public void should_resolve_container_children_mappings() throws Exception {
        final AtomicBoolean firstCalled = new AtomicBoolean(false);
        final AtomicBoolean secondCalled = new AtomicBoolean(false);

        container.mapFactory(NoConstructor.class, new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                firstCalled.set(true);
                return new NoConstructor();
            }
        });

        CheflingContainer childContainer = container.createChildContainer();

        childContainer.mapFactory(NoMethodInterface.class, new CheflingFactory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(CheflingContainer container) {
                secondCalled.set(true);
                return new NoMethodImplementation();
            }
        });

        container.validateContainer();

        assertTrue(firstCalled.get());
        assertTrue(secondCalled.get());
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_unresolvable() throws Exception {
        container.mapFactory(NoConstructor.class, new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return null;
            }
        });

        container.validateContainer();
    }

}
