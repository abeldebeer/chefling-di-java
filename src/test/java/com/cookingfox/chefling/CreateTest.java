package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.TypeNotAllowedException;
import com.cookingfox.chefling.fixtures.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Container#create(Class)}.
 */
public class CreateTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test
    public void create_throws_when_type_not_instantiable() {
        for (Class type : notInstantiableTypes) {
            try {
                container.create(type);

                Assert.fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test
    public void create_resolves_no_constructor_class() throws ContainerException {
        NoConstructor result = container.create(NoConstructor.class);

        Assert.assertNotNull(result);
    }

    @Test
    public void create_resolves_one_param_constructor_class() throws ContainerException {
        OneParamConstructor result = container.create(OneParamConstructor.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.param);
    }

    @Test
    public void create_resolves_two_level_dependencies() throws ContainerException {
        TwoLevelDependencies result = container.create(TwoLevelDependencies.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.first);
        Assert.assertNotNull(result.second);
    }

    @Test
    public void create_multiple_constructors_selects_empty_constructor() throws ContainerException {
        MultipleConstructorsTargetEmpty result = container.create(MultipleConstructorsTargetEmpty.class);

        Assert.assertNotNull(result);
        Assert.assertNull(result.first);
        Assert.assertNull(result.second);
    }

    @Test
    public void create_multiple_constructors_selects_allowed_constructor() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        MultipleConstructorsTargetAllowed result = container.create(MultipleConstructorsTargetAllowed.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.first);
        Assert.assertNotNull(result.second);
        Assert.assertNull(result.third);
        Assert.assertNull(result.fourth);
    }

    @Test
    public void create_should_use_subtype_mapping() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        Assert.assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void create_doubly_mapped_type() throws ContainerException {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void create_quadruply_mapped_type() throws ContainerException {
        container.mapType(D.class, E.class);
        container.mapType(C.class, D.class);
        container.mapType(B.class, C.class);
        container.mapType(A.class, B.class);

        Object result = container.create(A.class);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof E);
    }

}
