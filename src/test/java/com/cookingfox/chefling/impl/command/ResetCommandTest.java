package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class ResetCommandTest extends AbstractTest {

    @Test
    public void reset_removes_mappings_and_instances() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.get(NoMethodInterface.class);

        container.reset();

        assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void reset_reinitializes_container() throws Exception {
        assertTrue(container.has(Container.class));

        container.reset();

        assertTrue(container.has(Container.class));
    }

}
