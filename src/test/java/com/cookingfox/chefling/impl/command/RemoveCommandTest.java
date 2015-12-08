package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import com.cookingfox.fixtures.chefling.QuadruplyTyped;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link RemoveCommandImpl}.
 */
public class RemoveCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void remove_should_throw_if_type_null() throws Exception {
        container.remove(null);
    }

    @Test
    public void remove_should_not_throw_for_no_mapping() throws Exception {
        container.remove(NoConstructor.class);
    }

    @Test
    public void remove_should_remove_stored_instance() throws Exception {
        container.get(NoConstructor.class);
        container.remove(NoConstructor.class);

        assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_should_remove_mapped_instance() throws Exception {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_should_remove_subtype_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void remove_should_remove_factory_mapping() throws Exception {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(Container container) throws ContainerException {
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_should_throw_for_container_types() {
        Class[] containerTypes = {Container.class, CommandContainer.class};

        for (Class type : containerTypes) {
            try {
                container.remove(type);

                fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof RemoveTypeNotAllowedException);
            }
        }
    }

    @Test
    public void remove_quadruply_mapped_types_top_should_remove_top() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.remove(QuadruplyTyped.A.class);

        assertTrue("has D", container.has(QuadruplyTyped.D.class));
        assertTrue("has C", container.has(QuadruplyTyped.C.class));
        assertTrue("has B", container.has(QuadruplyTyped.B.class));
        assertFalse("not has A", container.has(QuadruplyTyped.A.class));
    }

    @Test
    public void remove_quadruply_mapped_types_root_should_remove_all() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.remove(QuadruplyTyped.D.class);

        assertFalse("not has D", container.has(QuadruplyTyped.D.class));
        assertFalse("not has C", container.has(QuadruplyTyped.C.class));
        assertFalse("not has B", container.has(QuadruplyTyped.B.class));
        assertFalse("not has A", container.has(QuadruplyTyped.A.class));
    }

    @Test
    public void remove_quadruply_mapped_types_root_should_remove_all_from_children() throws Exception {
        Container childA = new CommandContainer();
        childA.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.addChild(childA);

        Container childB = new CommandContainer();
        childB.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        childA.addChild(childB);

        Container childC = new CommandContainer();
        childC.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        childB.addChild(childC);

        Container childD = new CommandContainer();
        childD.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);
        childC.addChild(childD);

        container.remove(QuadruplyTyped.D.class);

        assertFalse("not has D", container.has(QuadruplyTyped.D.class));
        assertFalse("not has C", container.has(QuadruplyTyped.C.class));
        assertFalse("not has B", container.has(QuadruplyTyped.B.class));
        assertFalse("not has A", container.has(QuadruplyTyped.A.class));
    }

    @Test
    public void remove_should_remove_mapping_from_parent() throws Exception {
        Container parentContainer = new CommandContainer();
        parentContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.setParent(parentContainer);

        assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void remove_should_remove_mapping_from_child() throws Exception {
        Container childContainer = new CommandContainer();
        childContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.addChild(childContainer);

        assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        assertFalse(container.has(NoMethodInterface.class));
    }

}
