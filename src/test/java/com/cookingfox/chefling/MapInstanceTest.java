package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodAbstract;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * Test cases for {@link Container#mapInstance(Class, Object)}.
 */
public class MapInstanceTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullValueNotAllowedException.class)
    public void mapInstance_throws_if_type_null() throws ContainerException {
        container.mapInstance(null, new Object());
    }

    @Test(expected = NullValueNotAllowedException.class)
    public void mapInstance_throws_if_instance_null() throws ContainerException {
        container.mapInstance(getClass(), null);
    }

    @Test
    public void mapInstance_throws_if_type_not_allowed() {
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

    @Test(expected = TypeMappingAlreadyExistsException.class)
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

    @Test
    public void mapInstance_passes_concurrency_test() throws ContainerException {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        Assert.assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

}
