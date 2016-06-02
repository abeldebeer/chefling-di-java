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
 * Unit tests for {@link SetParentContainerCommandImpl}.
 */
public class SetParentContainerCommandImplTest extends AbstractTest {

    @Test(expected = ContainerException.class)
    public void should_throw_if_null() throws Exception {
        container.setParentContainer(null);
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_self() throws Exception {
        container.setParentContainer(container);
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_already_set() throws Exception {
        container.setParentContainer(new CommandContainer());
        container.setParentContainer(new CommandContainer());
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_not_CommandContainer() throws Exception {
        container.setParentContainer(new NoopContainer());
    }

    @Test
    public void should_set_parent() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        container.setParentContainer(parentContainer);

        assertSame(parentContainer, container.parent);
    }

    @Test
    public void should_add_current_as_child() throws Exception {
        CommandContainer parentContainer = new CommandContainer();
        container.setParentContainer(parentContainer);

        assertTrue(parentContainer.children.contains(container));
    }

    @Test(expected = ContainerException.class)
    public void should_throw_if_mapping_conflict() throws Exception {
        container.getInstance(NoConstructor.class);

        CheflingContainer parentContainer = new CommandContainer();
        parentContainer.mapInstance(NoConstructor.class, new NoConstructor());

        container.setParentContainer(parentContainer);
    }

}
