package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

/**
 * Tests for the ContainerChildren class.
 */
public class ContainerSetTest extends AbstractTest {

    protected ContainerSet children;

    //----------------------------------------------------------------------------------------------
    // TEST LIFECYCLE
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        children = new ContainerSet();
    }

    @After
    public void tearDown() throws Exception {
        children = null;
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: ADD
    //----------------------------------------------------------------------------------------------

    @Test
    public void addChild_passes_concurrency_test() throws ContainerException {
        int numTests = 50;
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();
        final Container childContainer = new Container();

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    children.add(childContainer);
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        Assert.assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: HAS
    //----------------------------------------------------------------------------------------------

    @Test
    public void has_returns_false_if_null() {
        boolean result = children.has(null);

        Assert.assertFalse(result);
    }

    @Test
    public void has_returns_false_if_no_child() {
        boolean result = children.has(new Container());

        Assert.assertFalse(result);
    }

    @Test
    public void has_returns_true_if_has_child() throws ContainerException {
        Container childContainer = new Container();
        children.add(childContainer);

        boolean result = children.has(childContainer);

        Assert.assertTrue(result);
    }

}
