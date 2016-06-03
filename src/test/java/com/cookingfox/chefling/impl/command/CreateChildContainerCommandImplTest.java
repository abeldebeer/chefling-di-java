package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CreateChildContainerCommandImpl}.
 */
public class CreateChildContainerCommandImplTest extends AbstractTest {

    @Test
    public void should_create_a_new_container() throws Exception {
        CheflingContainer child = container.createChildContainer();

        assertNotNull(child);
    }

    @Test
    public void should_add_child() throws Exception {
        NoMethodInterface instance = new NoMethodImplementation();

        CheflingContainer child = container.createChildContainer();
        child.mapInstance(NoMethodInterface.class, instance);

        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertSame(instance, result);

        try {
            container.addChildContainer(child);
        } catch (ContainerException e) {
            assertTrue(e.getMessage().toLowerCase()
                    .contains("child container has already been added"));
        }
    }

}
