package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ResetContainerCommandImpl}.
 */
public class ResetContainerCommandImplTest extends AbstractTest {

    @Test
    public void should_remove_mappings_and_instances() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.getInstance(NoMethodInterface.class);

        container.resetContainer();

        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

    @Test
    public void should_reinitialize_container() throws Exception {
        assertTrue(container.hasInstanceOrMapping(CheflingContainer.class));

        container.resetContainer();

        assertTrue(container.hasInstanceOrMapping(CheflingContainer.class));
    }

}
