package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link HasInstanceOrMappingCommandImpl}.
 */
public class HasInstanceOrMappingCommandImplTest extends AbstractTest {

    @Test
    public void should_return_false_if_null() throws Exception {
        assertFalse(container.hasInstanceOrMapping(null));
    }

    @Test
    public void should_return_false_if_no_instance_or_mapping() throws Exception {
        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

    @Test
    public void should_return_true_if_instance() throws Exception {
        container.getInstance(NoConstructor.class);

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void should_return_true_if_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertTrue(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

}
