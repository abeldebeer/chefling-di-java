package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

/**
 * Unit tests for {@link MapFactoryCommandImpl}.
 */
public class MapFactoryCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void mapFactory_should_throw_if_type_null() throws Exception {
        Factory factory = new Factory() {
            @Override
            public Object createInstance(Container container) throws ContainerException {
                return null;
            }
        };

        container.mapFactory(null, factory);
    }

    @Test(expected = NullValueNotAllowedException.class)
    public void mapFactory_should_throw_if_factory_null() throws Exception {
        container.mapFactory(getClass(), null);
    }

    @Test
    public void mapFactory_should_throw_if_type_not_allowed() throws Exception {
        Factory factory = new Factory() {
            @Override
            public Object createInstance(Container container) throws ContainerException {
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

    @SuppressWarnings("all")
    @Test(expected = FactoryIncorrectGenericException.class)
    public void mapFactory_should_throw_for_incorrect_generic_type() throws Exception {
        Factory<String> incorrectGeneric = new Factory<String>() {
            @Override
            public String createInstance(Container container) throws ContainerException {
                return "foo";
            }
        };
        Factory wrapper = incorrectGeneric;

        container.mapFactory(NoMethodInterface.class, wrapper);
    }

    @SuppressWarnings("all")
    @Test
    public void mapFactory_should_accept_generic_sub_type() throws Exception {
        final NoMethodImplementation instance = new NoMethodImplementation();

        Factory<NoMethodImplementation> genericSubType = new Factory<NoMethodImplementation>() {
            @Override
            public NoMethodImplementation createInstance(Container container) throws ContainerException {
                return instance;
            }
        };

        Factory wrapper = genericSubType;

        container.mapFactory(NoMethodInterface.class, wrapper);

        NoMethodInterface result = container.get(NoMethodInterface.class);

        Assert.assertSame(instance, result);
    }

    @Test
    public void mapFactory_simple_should_resolve_expected() throws Exception {
        final LinkedList<Integer> callHashCodes = new LinkedList<>();

        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) throws ContainerException {
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
    public void mapFactory_should_throw_if_returns_null() throws Exception {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) throws ContainerException {
                return null;
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.get(NoConstructor.class);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = FactoryReturnedUnexpectedValueException.class)
    public void mapFactory_should_throw_if_returns_invalid() throws Exception {
        Factory factory = new Factory() {
            @Override
            public Object createInstance(Container container) throws ContainerException {
                return "some unexpected value";
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.get(NoConstructor.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapFactory_should_throw_if_type_already_mapped() throws Exception {
        Factory<NoMethodInterface> factory = new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(Container container) throws ContainerException {
                return null;
            }
        };

        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapFactory(NoMethodInterface.class, factory);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapFactory_should_throw_if_has_instance() throws Exception {
        Factory<NoConstructor> factory = new Factory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(Container container) throws ContainerException {
                return null;
            }
        };

        container.get(NoConstructor.class);
        container.mapFactory(NoConstructor.class, factory);
    }

    @Test
    public void mapFactory_should_pass_concurrency_test() throws Exception {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<>();
        final Factory<NoMethodInterface> factory = new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(Container container) throws ContainerException {
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
