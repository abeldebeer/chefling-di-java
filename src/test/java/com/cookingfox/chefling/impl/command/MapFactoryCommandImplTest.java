package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.exception.*;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link MapFactoryCommandImpl}.
 */
public class MapFactoryCommandImplTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void should_throw_if_type_null() throws Exception {
        CheflingFactory factory = new CheflingFactory() {
            @Override
            public Object createInstance(CheflingContainer container) {
                return null;
            }
        };

        container.mapFactory(null, factory);
    }

    @Test(expected = NullValueNotAllowedException.class)
    public void should_throw_if_factory_null() throws Exception {
        container.mapFactory(getClass(), null);
    }

    @Test
    public void should_throw_if_type_not_allowed() throws Exception {
        CheflingFactory factory = new CheflingFactory() {
            @Override
            public Object createInstance(CheflingContainer container) {
                return null;
            }
        };

        for (Map.Entry<Class, Object> entry : NOT_ALLOWED_INSTANCES.entrySet()) {
            try {
                container.mapFactory(entry.getKey(), factory);

                Assert.fail("Did not receive expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_not_throw_for_missing_generic_type() throws Exception {
        CheflingFactory factory = new CheflingFactory() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);
    }

    @SuppressWarnings("all")
    @Test(expected = FactoryIncorrectGenericException.class)
    public void should_throw_for_incorrect_generic_type() throws Exception {
        CheflingFactory<String> incorrectGeneric = new CheflingFactory<String>() {
            @Override
            public String createInstance(CheflingContainer container) {
                return "foo";
            }
        };
        CheflingFactory wrapper = incorrectGeneric;

        container.mapFactory(NoMethodInterface.class, wrapper);
    }

    @SuppressWarnings("all")
    @Test
    public void should_accept_generic_sub_type() throws Exception {
        final NoMethodImplementation instance = new NoMethodImplementation();

        CheflingFactory<NoMethodImplementation> genericSubType = new CheflingFactory<NoMethodImplementation>() {
            @Override
            public NoMethodImplementation createInstance(CheflingContainer container) {
                return instance;
            }
        };

        CheflingFactory wrapper = genericSubType;

        container.mapFactory(NoMethodInterface.class, wrapper);

        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        Assert.assertSame(instance, result);
    }

    @Test
    public void should_not_throw_for_generic_factory_impl() throws Exception {
        NoConstructor instance = new NoConstructor();

        container.mapFactory(NoConstructor.class, new GenericInstanceFactory<>(instance));

        NoConstructor result = container.getInstance(NoConstructor.class);

        Assert.assertSame(instance, result);
    }

    @Test
    public void should_not_throw_for_anonymous_implementation_of_existing_factory() throws Exception {
        NoConstructorFactory factory = new NoConstructorFactory() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return super.createInstance(container);
            }
        };

        container.mapFactory(NoConstructor.class, factory);
    }

    @Test(expected = FactoryIncorrectGenericException.class)
    public void should_throw_for_incorrect_anonymous_implementation_of_existing_factory() throws Exception {
        NoConstructorFactory factory = new NoConstructorFactory() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return super.createInstance(container);
            }
        };

        // noinspection unchecked
        container.mapFactory(OneParamConstructor.class, (CheflingFactory) factory);
    }

    @Test
    public void should_resolve_simple() throws Exception {
        final LinkedList<Integer> callHashCodes = new LinkedList<>();

        CheflingFactory<NoConstructor> factory = new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                callHashCodes.add(hashCode());
                return new NoConstructor();
            }
        };

        container.mapFactory(NoConstructor.class, factory);

        NoConstructor result = container.getInstance(NoConstructor.class);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, callHashCodes.size());
    }

    @Test(expected = FactoryReturnedNullException.class)
    public void should_throw_if_returns_null() throws Exception {
        CheflingFactory<NoConstructor> factory = new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return null;
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.getInstance(NoConstructor.class);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = FactoryReturnedUnexpectedValueException.class)
    public void should_throw_if_returns_invalid() throws Exception {
        CheflingFactory factory = new CheflingFactory() {
            @Override
            public Object createInstance(CheflingContainer container) {
                return "some unexpected value";
            }
        };

        container.mapFactory(NoConstructor.class, factory);
        container.getInstance(NoConstructor.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void should_throw_if_type_already_mapped() throws Exception {
        CheflingFactory<NoMethodInterface> factory = new CheflingFactory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(CheflingContainer container) {
                return null;
            }
        };

        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.mapFactory(NoMethodInterface.class, factory);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void should_throw_if_has_instance() throws Exception {
        CheflingFactory<NoConstructor> factory = new CheflingFactory<NoConstructor>() {
            @Override
            public NoConstructor createInstance(CheflingContainer container) {
                return null;
            }
        };

        container.getInstance(NoConstructor.class);
        container.mapFactory(NoConstructor.class, factory);
    }

    @Test
    public void should_pass_concurrency_test() throws Exception {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<>();
        final CheflingFactory<NoMethodInterface> factory = new CheflingFactory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(CheflingContainer container) {
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

    //----------------------------------------------------------------------------------------------
    // TESTS: getGenericType
    //----------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Test
    public void getGenericType_should_return_null_for_root_super_class() throws Exception {
        MapFactoryCommandImpl mapFactoryCommand = new MapFactoryCommandImpl(container);

        Class result = mapFactoryCommand.getGenericType((Class) NoConstructor.class);

        assertNull(result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getGenericType_should_return_null_for_object_class() throws Exception {
        MapFactoryCommandImpl mapFactoryCommand = new MapFactoryCommandImpl(container);

        Class result = mapFactoryCommand.getGenericType((Class) Object.class);

        assertNull(result);
    }

}
