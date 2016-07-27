package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.chefling.api.exception.TypeNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CreateInstanceCommandImpl}.
 */
public class CreateInstanceCommandImplTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void should_throw_if_type_null() throws Exception {
        container.createInstance(null);
    }

    @Test
    public void should_throw_if_type_not_instantiable() {
        for (Class type : notInstantiableTypes) {
            try {
                container.createInstance(type);

                fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test
    public void should_resolve_no_constructor_class() throws Exception {
        NoConstructor result = container.createInstance(NoConstructor.class);

        assertNotNull(result);
    }

    @Test
    public void should_resolve_one_param_constructor_class() throws Exception {
        OneParamConstructor result = container.createInstance(OneParamConstructor.class);

        assertNotNull(result);
        assertNotNull(result.param);
    }

    @Test
    public void should_resolve_two_level_dependencies() throws Exception {
        TwoLevelDependencies result = container.createInstance(TwoLevelDependencies.class);

        assertNotNull(result);
        assertNotNull(result.first);
        assertNotNull(result.second);
    }

    @Test
    public void multiple_constructors_should_select_empty_constructor() throws Exception {
        MultipleConstructorsVaryingParametersTargetEmpty result =
                container.createInstance(MultipleConstructorsVaryingParametersTargetEmpty.class);

        assertNotNull(result);
        assertNull(result.first);
        assertNull(result.second);
    }

    @Test
    public void multiple_constructors_should_select_allowed_constructor() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        MultipleConstructorsVaryingParametersTargetAllowed result =
                container.createInstance(MultipleConstructorsVaryingParametersTargetAllowed.class);

        assertNotNull(result);
        assertNotNull(result.first);
        assertNotNull(result.second);
        assertNull(result.third);
        assertNull(result.fourth);
    }

    @Test
    public void multiple_constructors_single_parameter_should_select_allowed() throws Exception {
        Object result = container.createInstance(MultipleConstructorsSingleParameterOneResolvable.class);

        assertNotNull(result);
    }

    @Test
    public void should_use_subtype_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.createInstance(NoMethodInterface.class);

        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void should_resolve_doubly_mapped_type() throws Exception {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        NoMethodInterface result = container.createInstance(NoMethodInterface.class);

        assertNotNull(result);
        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void should_resolve_quadruply_mapped_type() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        Object result = container.createInstance(QuadruplyTyped.A.class);

        assertNotNull(result);
        assertTrue(result instanceof QuadruplyTyped.E);
    }

    /**
     * Note: this test is just for checking the exception output.
     */
    @Test
    public void should_provide_expected_stack_trace() throws Exception {
        try {
            container.createInstance(ThrowingConstructor.class);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    /**
     * Note: this test is just for checking the exception output.
     */
    @Test
    public void should_create_proper_error_message() throws Exception {
        try {
            container.createInstance(RequiresProperErrorMessage.class);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void should_use_type_cache() throws Exception {
        assertFalse(CreateInstanceCommandImpl.PARAM_CACHE.containsKey(OneParamConstructor.class));
        assertFalse(CreateInstanceCommandImpl.PARAM_CACHE.containsKey(NoConstructor.class));

        // depends on `NoConstructor`
        container.createInstance(OneParamConstructor.class);

        assertTrue(CreateInstanceCommandImpl.PARAM_CACHE.containsKey(OneParamConstructor.class));
        assertTrue(CreateInstanceCommandImpl.PARAM_CACHE.containsKey(NoConstructor.class));

        // use cached constructor parameters
        container.createInstance(OneParamConstructor.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: createInstanceUsingCache
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructInstance_should_return_instance_created_with_type_cache() throws Exception {
        CreateInstanceCommandImpl createInstanceCommand = new CreateInstanceCommandImpl(container);

        createInstanceCommand.createInstanceUsingCache(NoConstructor.class);

        assertTrue(CreateInstanceCommandImpl.PARAM_CACHE.containsKey(NoConstructor.class));

        NoConstructor createdWithCache = createInstanceCommand.createInstanceUsingCache(NoConstructor.class);

        assertNotNull(createdWithCache);
    }

}
