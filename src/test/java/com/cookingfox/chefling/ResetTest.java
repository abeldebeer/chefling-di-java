package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Container#reset()}.
 */
public class ResetTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
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

}
