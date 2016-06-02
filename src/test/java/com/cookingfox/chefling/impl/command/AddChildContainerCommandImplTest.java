package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoopContainer;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AddChildContainerCommandImpl}.
 */
public class AddChildContainerCommandImplTest extends AbstractTest {

    @Test(expected = ContainerException.class)
    public void should_throw_if_null() throws Exception {
        container.addChildContainer(null);
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_self() throws Exception {
        container.addChildContainer(container);
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_not_CommandContainer() throws Exception {
        container.addChildContainer(new NoopContainer());
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_already_added() throws Exception {
        CheflingContainer child = new CommandContainer();
        container.addChildContainer(child);
        container.addChildContainer(child);
    }

    @Test
    public void should_add_to_children() throws Exception {
        CommandContainer child = new CommandContainer();
        container.addChildContainer(child);

        assertTrue(container.children.contains(child));
    }

    @Test
    public void should_set_parent_to_self() throws Exception {
        CommandContainer child = new CommandContainer();
        container.addChildContainer(child);

        assertSame(container, child.parent);
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_mapping_conflict() throws Exception {
        container.mapInstance(NoConstructor.class, new NoConstructor());

        CommandContainer child = new CommandContainer();
        child.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChildContainer(child);
    }

}
