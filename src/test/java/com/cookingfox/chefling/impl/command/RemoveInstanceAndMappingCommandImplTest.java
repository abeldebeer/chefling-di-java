package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link RemoveInstanceAndMappingCommandImpl}.
 */
public class RemoveInstanceAndMappingCommandImplTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void should_throw_if_type_null() throws Exception {
        container.removeInstanceAndMapping(null);
    }

    @Test
    public void should_not_throw_for_no_mapping() throws Exception {
        container.removeInstanceAndMapping(NoConstructor.class);
    }

    @Test
    public void should_remove_stored_instance() throws Exception {
        container.getInstance(NoConstructor.class);

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));

        container.removeInstanceAndMapping(NoConstructor.class);

        assertFalse(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void should_remove_mapped_instance() throws Exception {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));

        container.removeInstanceAndMapping(NoConstructor.class);

        assertFalse(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void should_remove_subtype_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        assertTrue(container.hasInstanceOrMapping(NoMethodInterface.class));

        container.removeInstanceAndMapping(NoMethodInterface.class);

        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

    @Test
    public void should_remove_factory_mapping() throws Exception {
        CheflingFactory<NoConstructor> factory = new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        assertTrue(container.hasInstanceOrMapping(NoConstructor.class));

        container.removeInstanceAndMapping(NoConstructor.class);

        assertFalse(container.hasInstanceOrMapping(NoConstructor.class));
    }

    @Test
    public void should_throw_for_container_types() {
        Class[] containerTypes = {CheflingContainer.class, CommandContainer.class};

        for (Class type : containerTypes) {
            try {
                container.removeInstanceAndMapping(type);

                fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof RemoveTypeNotAllowedException);
            }
        }
    }

    @Test
    public void should_remove_top_for_quadruply_mapped_types_top() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.removeInstanceAndMapping(QuadruplyTyped.A.class);

        assertTrue("has D", container.hasInstanceOrMapping(QuadruplyTyped.D.class));
        assertTrue("has C", container.hasInstanceOrMapping(QuadruplyTyped.C.class));
        assertTrue("has B", container.hasInstanceOrMapping(QuadruplyTyped.B.class));
        assertFalse("not has A", container.hasInstanceOrMapping(QuadruplyTyped.A.class));
    }

    @Test(expected = RemoveTypeNotAllowedException.class)
    public void should_throw_for_remove_quadruply_mapped_types_root() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.removeInstanceAndMapping(QuadruplyTyped.D.class);
    }

    @Test(expected = RemoveTypeNotAllowedException.class)
    public void should_throw_for_remove_quadruply_mapped_types_children_root() throws Exception {
        CheflingContainer childA = new CommandContainer();
        childA.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.addChildContainer(childA);

        CheflingContainer childB = new CommandContainer();
        childB.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        childA.addChildContainer(childB);

        CheflingContainer childC = new CommandContainer();
        childC.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        childB.addChildContainer(childC);

        CheflingContainer childD = new CommandContainer();
        childD.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);
        childC.addChildContainer(childD);

        container.removeInstanceAndMapping(QuadruplyTyped.D.class);
    }

    @Test
    public void should_remove_mapping_from_parent() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        parentContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.setParentContainer(parentContainer);

        assertTrue(container.hasInstanceOrMapping(NoMethodInterface.class));

        container.removeInstanceAndMapping(NoMethodInterface.class);

        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

    @Test
    public void should_remove_mapping_from_child() throws Exception {
        CheflingContainer childContainer = new CommandContainer();
        childContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.addChildContainer(childContainer);

        assertTrue(container.hasInstanceOrMapping(NoMethodInterface.class));

        container.removeInstanceAndMapping(NoMethodInterface.class);

        assertFalse(container.hasInstanceOrMapping(NoMethodInterface.class));
    }

    @Test(expected = RemoveTypeNotAllowedException.class)
    public void should_throw_if_has_alias() throws Exception {
        container.mapType(InterfaceSegregation.Person.class, InterfaceSegregation.JohnDoe.class);
        container.mapType(InterfaceSegregation.Talkable.class, InterfaceSegregation.Person.class);
        container.mapType(InterfaceSegregation.Walkable.class, InterfaceSegregation.Person.class);
        container.removeInstanceAndMapping(InterfaceSegregation.Person.class);
    }

}
