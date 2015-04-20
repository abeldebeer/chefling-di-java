package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.NoConstructor;
import com.cookingfox.chefling.fixtures.NoMethodAbstract;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 20/04/15.
 */
public class MapInstanceTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test
    public void mapInstance_throws_when_type_not_allowed() {
        for (Map.Entry<Class, Object> entry : getNotAllowedInstances().entrySet()) {
            try {
                container.mapInstance(entry.getKey(), entry.getValue());

                Assert.fail("Did not get expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = NotAnInstanceOfTypeException.class)
    public void mapInstance_throws_if_instance_not_instanceof_type() throws ContainerException {
        // use variable to prevent generic warning
        Class type = NoConstructor.class;

        container.mapInstance(type, new Object());
    }

    @Test
    public void mapInstance_stores_instance() throws ContainerException {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertSame(instance, result);
    }

    @Test(expected = ReplaceInstanceNotAllowedException.class)
    public void mapInstance_throws_if_stored_instance_default() throws ContainerException {
        container.mapInstance(NoConstructor.class, new NoConstructor());
        container.mapInstance(NoConstructor.class, new NoConstructor());
    }

    @Test
    public void mapInstance_accepts_implementation() throws ContainerException {
        NoMethodImplementation instance = new NoMethodImplementation();

        container.mapInstance(NoMethodInterface.class, instance);
        container.mapInstance(NoMethodAbstract.class, instance);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapInstance_first_mapType_then_mapInstance_throws() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
    }

}
