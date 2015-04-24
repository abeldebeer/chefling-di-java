package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.TypeNotAllowedException;
import com.cookingfox.chefling.fixtures.NoConstructor;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Container#remove(Class)}.
 */
public class RemoveTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test
    public void remove_no_value_does_not_throw() throws ContainerException {
        container.remove(NoConstructor.class);
    }

    @Test
    public void remove_stored_instance_removes_instance() throws ContainerException {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        Assert.assertTrue(container.has(NoConstructor.class));

        container.remove(NoConstructor.class);

        Assert.assertFalse(container.has(NoConstructor.class));
    }

    @Test
    public void remove_subtype_mapping_removes_mapping() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        Assert.assertTrue(container.has(NoMethodInterface.class));

        container.remove(NoMethodInterface.class);

        Assert.assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void remove_factory_mapping_removes_mapping() throws ContainerException {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(ContainerInterface container) throws ContainerException {
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
        Class[] containerTypes = {Container.class, ContainerInterface.class};

        for (Class type : containerTypes) {
            try {
                container.remove(type);

                Assert.fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

}
