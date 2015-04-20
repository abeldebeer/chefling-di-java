package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * Tests all container functionality.
 */
public class ContainerTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'HAS' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void has_returns_false_if_no_instance_or_mapping() {
        boolean result = container.has(NoMethodInterface.class);

        Assert.assertFalse(result);
    }

    @Test
    public void has_returns_true_if_instance() throws ContainerException {
        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());

        boolean result = container.has(NoMethodInterface.class);

        Assert.assertTrue(result);
    }

    @Test
    public void has_returns_true_if_mapping() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        boolean result = container.has(NoMethodInterface.class);

        Assert.assertTrue(result);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'REMOVE' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void remove_stored_instance_removes_instance() throws ContainerException {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        Assert.assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_subtype_mapping_removes_mapping() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        Assert.assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        Assert.assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void remove_factory_mapping_removes_mapping() throws ContainerException {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(ContainerInterface container) throws ContainerException {
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        Assert.assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'GET DEFAULT' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void get_default_concurrent_uses_same_instance() {
        final Container defaultContainer = Container.getDefault();

        Runnable test = new Runnable() {
            public void run() {
                Assert.assertSame(defaultContainer, Container.getDefault());
            }
        };

        runConcurrencyTest(test, 5);
    }

}
