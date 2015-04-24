package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for general {@link Container} methods.
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
    // TEST CASES: 'RESET' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void reset_removes_mappings_and_instances() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.get(NoMethodInterface.class);

        container.reset();

        Assert.assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void reset_reinitializes_container() throws ContainerException {
        Assert.assertTrue(container.has(Container.class));

        container.reset();

        Assert.assertTrue(container.has(Container.class));
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
