package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link AddChildCommand}.
 */
public class AddChildCommandTest extends AbstractTest {

    @Test(expected = ContainerException.class)
    public void addChild_should_throw_if_null() throws Exception {
        container.addChild(null);
    }

    @Test(expected = ContainerException.class)
    public void addChild_should_throw_if_self() throws Exception {
        container.addChild(container);
    }

    @Test(expected = ContainerException.class)
    public void addChild_should_throw_if_not_CommandContainer() throws Exception {
        container.addChild(new NoopContainer());
    }

    @Test(expected = ContainerException.class)
    public void addChild_should_throw_if_already_added() throws Exception {
        Container child = new CommandContainer();
        container.addChild(child);
        container.addChild(child);
    }

    @Test
    public void addChild_should_add_to_children() throws Exception {
        CommandContainer child = new CommandContainer();
        container.addChild(child);

        assertTrue(container.children.contains(child));
    }

    @Test
    public void addChild_should_set_parent_to_self() throws Exception {
        CommandContainer child = new CommandContainer();
        container.addChild(child);

        assertSame(container, child.parent);
    }

    @Test(expected = ContainerException.class)
    public void addChild_should_throw_if_mapping_conflict() throws Exception {
        container.mapInstance(NoConstructor.class, new NoConstructor());

        CommandContainer child = new CommandContainer();
        child.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(child);
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    class NoopContainer implements Container {
        @Override
        public void addChild(Container container) throws ContainerException {

        }

        @Override
        public <T> T create(Class<T> type) throws ContainerException {
            return null;
        }

        @Override
        public <T> T get(Class<T> type) throws ContainerException {
            return null;
        }

        @Override
        public boolean has(Class type) {
            return false;
        }

        @Override
        public <T> void mapFactory(Class<T> type, Factory<T> factory) throws ContainerException {

        }

        @Override
        public <T> void mapInstance(Class<T> type, T instance) throws ContainerException {

        }

        @Override
        public <T> void mapType(Class<T> type, Class<? extends T> subType) throws ContainerException {

        }

        @Override
        public void remove(Class type) throws ContainerException {

        }

        @Override
        public void reset() {

        }
    }

}
