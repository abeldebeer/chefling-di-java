package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 11/06/15.
 */
public class ChildrenTest {

    protected ContainerChildren children;

    //----------------------------------------------------------------------------------------------
    // TEST LIFECYCLE
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        children = new ContainerChildren();
    }

    @After
    public void tearDown() throws Exception {
        children = null;
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: HAS CHILD
    //----------------------------------------------------------------------------------------------

    @Test
    public void hasChild_returns_false_if_null() {
        boolean result = children.hasChild(null);

        Assert.assertFalse(result);
    }

    @Test
    public void hasChild_returns_false_if_no_child() {
        boolean result = children.hasChild(new Container());

        Assert.assertFalse(result);
    }

    @Test
    public void hasChild_returns_true_if_has_child() throws ContainerException {
        Container childContainer = new Container();
        children.addChild(childContainer);

        boolean result = children.hasChild(childContainer);

        Assert.assertTrue(result);
    }

}
