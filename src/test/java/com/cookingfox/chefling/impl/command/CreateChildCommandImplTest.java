package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CreateChildCommandImpl}.
 */
public class CreateChildCommandImplTest extends AbstractTest {

    @Test
    public void createChild_should_create_a_new_container() throws Exception {
        Container child = container.createChild();

        assertNotNull(child);
    }

    @Test
    public void createChild_should_add_child() throws Exception {
        final NoMethodInterface instance = new NoMethodImplementation();

        Container child = container.createChild();
        child.mapInstance(NoMethodInterface.class, instance);

        NoMethodInterface result = container.get(NoMethodInterface.class);

        assertSame(instance, result);

        try {
            container.addChild(child);
        } catch (ContainerException e) {
            assertTrue(e.getMessage().toLowerCase()
                    .contains("child container has already been added"));
        }
    }

}
