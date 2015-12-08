package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodAbstract;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link MapInstanceCommandImpl}.
 */
public class MapInstanceCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void mapInstance_throws_if_type_null() throws Exception {
        container.mapInstance(null, new Object());
    }

    @Test(expected = NullValueNotAllowedException.class)
    public void mapInstance_throws_if_instance_null() throws Exception {
        container.mapInstance(getClass(), null);
    }

    @Test
    public void mapInstance_throws_if_type_not_allowed() {
        for (Map.Entry<Class, Object> entry : getNotAllowedInstances().entrySet()) {
            try {
                container.mapInstance(entry.getKey(), entry.getValue());

                fail("Did not get expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = NotAnInstanceOfTypeException.class)
    public void mapInstance_throws_if_instance_not_instanceof_type() throws Exception {
        // use variable to prevent generic warning
        Class type = NoConstructor.class;

        container.mapInstance(type, new Object());
    }

    @Test
    public void mapInstance_stores_instance() throws Exception {
        NoConstructor instance = new NoConstructor();

        container.mapInstance(NoConstructor.class, instance);

        NoConstructor result = container.get(NoConstructor.class);

        assertSame(instance, result);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapInstance_throws_if_stored_instance_default() throws Exception {
        container.mapInstance(NoConstructor.class, new NoConstructor());
        container.mapInstance(NoConstructor.class, new NoConstructor());
    }

    @Test
    public void mapInstance_accepts_implementation() throws Exception {
        NoMethodImplementation instance = new NoMethodImplementation();

        container.mapInstance(NoMethodInterface.class, instance);
        container.mapInstance(NoMethodAbstract.class, instance);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapInstance_first_mapType_then_mapInstance_throws() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
    }

    @Test
    public void mapInstance_passes_concurrency_test() throws Exception {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<>();

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

        assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

}
