package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.chefling.api.exception.TypeNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CreateCommand}.
 */
public class CreateCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void create_throws_if_type_null() throws Exception {
        container.create(null);
    }

    @Test
    public void create_throws_if_type_not_instantiable() {
        for (Class type : notInstantiableTypes) {
            try {
                container.create(type);

                fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test
    public void create_resolves_no_constructor_class() throws Exception {
        NoConstructor result = container.create(NoConstructor.class);

        assertNotNull(result);
    }

    @Test
    public void create_resolves_one_param_constructor_class() throws Exception {
        OneParamConstructor result = container.create(OneParamConstructor.class);

        assertNotNull(result);
        assertNotNull(result.param);
    }

    @Test
    public void create_resolves_two_level_dependencies() throws Exception {
        TwoLevelDependencies result = container.create(TwoLevelDependencies.class);

        assertNotNull(result);
        assertNotNull(result.first);
        assertNotNull(result.second);
    }

    @Test
    public void create_multiple_constructors_selects_empty_constructor() throws Exception {
        MultipleConstructorsVaryingParametersTargetEmpty result = container.create(MultipleConstructorsVaryingParametersTargetEmpty.class);

        assertNotNull(result);
        assertNull(result.first);
        assertNull(result.second);
    }

    @Test
    public void create_multiple_constructors_selects_allowed_constructor() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        MultipleConstructorsVaryingParametersTargetAllowed result = container.create(MultipleConstructorsVaryingParametersTargetAllowed.class);

        assertNotNull(result);
        assertNotNull(result.first);
        assertNotNull(result.second);
        assertNull(result.third);
        assertNull(result.fourth);
    }

    @Test
    public void create_multiple_constructors_single_parameter_selects_allowed() throws Exception {
        Object result = container.create(MultipleConstructorsSingleParameterOneResolvable.class);

        assertNotNull(result);
    }

    @Test
    public void create_should_use_subtype_mapping() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void create_doubly_mapped_type() throws Exception {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        assertNotNull(result);
        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void create_quadruply_mapped_type() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        Object result = container.create(QuadruplyTyped.A.class);

        assertNotNull(result);
        assertTrue(result instanceof QuadruplyTyped.E);
    }

}
