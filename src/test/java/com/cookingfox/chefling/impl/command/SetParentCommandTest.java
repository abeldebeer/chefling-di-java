package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoopContainer;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 07/12/15.
 */
public class SetParentCommandTest extends AbstractTest {

    @Test(expected = ContainerException.class)
    public void setParent_should_throw_if_null() throws Exception {
        container.setParent(null);
    }

    @Test(expected = ContainerException.class)
    public void setParent_should_throw_if_self() throws Exception {
        container.setParent(container);
    }

    @Test(expected = ContainerException.class)
    public void setParent_should_throw_if_already_set() throws Exception {
        container.setParent(new CommandContainer());
        container.setParent(new CommandContainer());
    }

    @Test(expected = ContainerException.class)
    public void setParent_should_throw_if_not_CommandContainer() throws Exception {
        container.setParent(new NoopContainer());
    }

    @Test
    public void setParent_should_set_parent() throws Exception {
        Container parentContainer = new CommandContainer();
        container.setParent(parentContainer);

        assertSame(parentContainer, container.parent);
    }

    @Test
    public void setParent_should_add_current_as_child() throws Exception {
        CommandContainer parentContainer = new CommandContainer();
        container.setParent(parentContainer);

        assertTrue(parentContainer.children.contains(container));
    }

    @Test(expected = ContainerException.class)
    public void setParent_should_throw_if_mapping_conflict() throws Exception {
        container.get(NoConstructor.class);

        Container parentContainer = new CommandContainer();
        parentContainer.mapInstance(NoConstructor.class, new NoConstructor());

        container.setParent(parentContainer);
    }

}
