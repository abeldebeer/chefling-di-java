package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link TestCommandImpl}.
 */
public class TestCommandImplTest extends AbstractTest {

    @Test
    public void test_should_resolve_all_current_mappings() throws Exception {
        final AtomicBoolean factoryCalled = new AtomicBoolean(false);

        container.mapFactory(NoConstructor.class, new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) {
                factoryCalled.set(true);
                return new NoConstructor();
            }
        });

        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertFalse(container.has(NoMethodImplementation.class));

        container.test();

        assertTrue(factoryCalled.get());
        assertTrue(container.has(NoMethodImplementation.class));
    }

    @Test
    public void test_should_resolve_container_children_mappings() throws Exception {
        final AtomicBoolean firstCalled = new AtomicBoolean(false);
        final AtomicBoolean secondCalled = new AtomicBoolean(false);

        container.mapFactory(NoConstructor.class, new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) {
                firstCalled.set(true);
                return new NoConstructor();
            }
        });

        Container childContainer = container.createChild();

        childContainer.mapFactory(NoMethodInterface.class, new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(Container container) {
                secondCalled.set(true);
                return new NoMethodImplementation();
            }
        });

        container.test();

        assertTrue(firstCalled.get());
        assertTrue(secondCalled.get());
    }

    @Test(expected = ContainerException.class)
    public void test_should_throw_if_unresolvable() throws Exception {
        container.mapFactory(NoConstructor.class, new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) {
                return null;
            }
        });

        container.test();
    }

}
