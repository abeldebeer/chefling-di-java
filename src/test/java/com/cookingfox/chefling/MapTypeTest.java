package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.NoConstructor;
import com.cookingfox.chefling.fixtures.NoMethodAbstract;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * Test cases for {@link Container#mapType(Class, Class)}.
 */
public class MapTypeTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapType_throws_when_mapping_exists() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void mapType_throws_when_sub_type_same_as_type() throws ContainerException {
        container.mapType(NoMethodImplementation.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void mapType_throws_when_sub_type_not_extends_type() throws ContainerException {
        Class type = NoConstructor.class;
        Class subType = Object.class;

        container.mapType(type, subType);
    }

    @Test
    public void mapType_throws_when_base_type_not_allowed() throws ContainerException {
        for (Map.Entry<Class, Class> entry : getNotAllowedSubTypes().entrySet()) {
            try {
                container.mapType(entry.getKey(), entry.getValue());

                Assert.fail("Did not receive expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = TypeNotInstantiableException.class)
    public void mapType_throws_when_sub_type_not_instantiable() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapType_first_mapInstance_then_mapType_throws() throws ContainerException {
        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test
    public void mapType_allows_to_map_to_previous_mapping() throws ContainerException {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);
    }

    @Test
    public void mapType_passes_concurrency_test() {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        Assert.assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

}
