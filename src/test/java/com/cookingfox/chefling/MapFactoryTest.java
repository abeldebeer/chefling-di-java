package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.NoConstructor;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * Test cases for {@link Container#mapFactory(Class, Factory)}.
 */
public class MapFactoryTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test
    public void mapFactory_throws_if_type_not_allowed() {
        Factory factory = new Factory() {
            @Override
            public Object create(ContainerInterface container) throws ContainerException {
                return null;
            }
        };

        for (Map.Entry<Class, Object> entry : getNotAllowedInstances().entrySet()) {
            try {
                container.mapFactory(entry.getKey(), factory);

                Assert.fail("Did not receive expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test
    public void mapFactory_simple_resolves_expected() throws ContainerException {
        final LinkedList<Integer> callHashCodes = new LinkedList<Integer>();

        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(ContainerInterface container) throws ContainerException {
                callHashCodes.add(hashCode());
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, callHashCodes.size());
    }

    @Test(expected = FactoryReturnedNullException.class)
    public void mapFactory_throws_if_returns_null() throws ContainerException {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(ContainerInterface container) throws ContainerException {
                return null;
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.get(NoConstructor.class);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = FactoryReturnedUnexpectedValueException.class)
    public void mapFactory_throws_if_returns_invalid() throws ContainerException {
        Factory factory = new Factory() {
            @Override
            public Object create(ContainerInterface container) throws ContainerException {
                return "some unexpected value";
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.get(NoConstructor.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapFactory_throws_if_type_already_mapped() throws ContainerException {
        Factory<NoMethodInterface> factory = new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface create(ContainerInterface container) throws ContainerException {
                return null;
            }
        };

        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapFactory(NoMethodInterface.class, factory);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapFactory_throws_if_type_already_set() throws ContainerException {
        Factory<NoMethodInterface> factory = new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface create(ContainerInterface container) throws ContainerException {
                return null;
            }
        };

        container.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
        container.mapFactory(NoMethodInterface.class, factory);
    }

    @Test
    public void mapFactory_passes_concurrency_test() throws ContainerException {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();
        final Factory<NoMethodInterface> factory = new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface create(ContainerInterface container) throws ContainerException {
                return null;
            }
        };

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.mapFactory(NoMethodInterface.class, factory);
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        Assert.assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

}
