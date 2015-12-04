package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class HasCommandTest extends AbstractTest {

    @Test
    public void has_should_return_false_if_null() throws Exception {
        assertFalse(container.has(null));
    }

    @Test
    public void has_should_return_false_if_no_instance_or_mapping() throws Exception {
        assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void has_should_return_true_if_instance() throws Exception {
        container.get(NoConstructor.class);

        assertTrue(container.has(NoConstructor.class));
    }

    @Test
    public void has_should_return_true_if_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertTrue(container.has(NoMethodInterface.class));
    }

}
