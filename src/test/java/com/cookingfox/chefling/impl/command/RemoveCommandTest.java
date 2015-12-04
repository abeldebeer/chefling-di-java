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
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class RemoveCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void remove_throws_if_type_null() throws Exception {
        container.remove(null);
    }

    @Test
    public void remove_no_value_does_not_throw() throws Exception {
        container.remove(NoConstructor.class);
    }

    @Test
    public void remove_stored_instance_removes_instance() throws Exception {
        container.get(NoConstructor.class);
        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_mapped_instance_removes_mapping() throws Exception {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        Assert.assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_subtype_mapping_removes_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        Assert.assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        Assert.assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void remove_factory_mapping_removes_mapping() throws Exception {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(Container container) throws ContainerException {
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        Assert.assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_container_should_throw() {
        Class[] containerTypes = {Container.class, CommandContainer.class};

        for (Class type : containerTypes) {
            try {
                container.remove(type);

                Assert.fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof RemoveTypeNotAllowedException);
            }
        }
    }

    @Test
    public void remove_quadruply_mapped_types_top_removes_top() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.remove(QuadruplyTyped.A.class);

        Assert.assertTrue("has D", container.has(QuadruplyTyped.D.class));
        Assert.assertTrue("has C", container.has(QuadruplyTyped.C.class));
        Assert.assertTrue("has B", container.has(QuadruplyTyped.B.class));
        Assert.assertFalse("not has A", container.has(QuadruplyTyped.A.class));
    }

    @Test
    public void remove_quadruply_mapped_types_root_removes_all() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        container.remove(QuadruplyTyped.D.class);

        Assert.assertFalse("not has D", container.has(QuadruplyTyped.D.class));
        Assert.assertFalse("not has C", container.has(QuadruplyTyped.C.class));
        Assert.assertFalse("not has B", container.has(QuadruplyTyped.B.class));
        Assert.assertFalse("not has A", container.has(QuadruplyTyped.A.class));
    }

}
