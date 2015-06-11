package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.NullValueNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Container#get(Class)}.
 */
public class GetTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullValueNotAllowedException.class)
    public void get_throws_if_type_null() throws ContainerException {
        container.get(null);
    }

    @Test
    public void get_creates_type_instance() throws ContainerException {
        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertNotNull(result);
    }

    @Test
    public void get_returns_same_instance() throws ContainerException {
        NoConstructor firstResult = container.get(NoConstructor.class);
        NoConstructor secondResult = container.get(NoConstructor.class);
        NoConstructor thirdResult = container.get(NoConstructor.class);

        Assert.assertNotNull(firstResult);
        Assert.assertSame(firstResult, secondResult);
        Assert.assertSame(firstResult, thirdResult);
        Assert.assertSame(secondResult, thirdResult);
    }

    @Test
    public void get_container_returns_self() throws ContainerException {
        Container byClass = container.get(Container.class);
        ContainerInterface byInterface = container.get(ContainerInterface.class);

        Assert.assertSame(container, byClass);
        Assert.assertSame(container, byInterface);
    }

    @Test
    public void get_mapped_type_creates_mapped() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.get(NoMethodInterface.class);

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof NoMethodImplementation);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_self() throws ContainerException {
        container.get(CircularSelf.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_simple() throws ContainerException {
        container.get(CircularSimple.A.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_complex() throws ContainerException {
        container.mapType(CircularComplex.CInterface.class, CircularComplex.C.class);
        container.get(CircularComplex.A.class);
    }

    @Test
    public void get_doubly_mapped_type_returns_same_instance() throws ContainerException {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        Object first = container.get(NoMethodAbstract.class);
        Object second = container.get(NoMethodInterface.class);

        Assert.assertSame(first, second);
    }

    @Test
    public void get_quadruply_mapped_type_returns_same_instance() throws ContainerException {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        Object d = container.get(QuadruplyTyped.D.class);
        Object c = container.get(QuadruplyTyped.C.class);
        Object b = container.get(QuadruplyTyped.B.class);
        Object a = container.get(QuadruplyTyped.A.class);

        Assert.assertSame(d, c);
        Assert.assertSame(c, b);
        Assert.assertSame(b, a);
    }

    @Test
    public void get_multiple_mapped_interfaces_returns_same_instance() throws ContainerException {
        container.mapType(InterfaceSegregation.Person.class, InterfaceSegregation.JohnDoe.class);
        container.mapType(InterfaceSegregation.Talkable.class, InterfaceSegregation.Person.class);
        container.mapType(InterfaceSegregation.Walkable.class, InterfaceSegregation.Person.class);

        Object instanceFromMainInterface = container.get(InterfaceSegregation.Person.class);
        Object instanceFromExtendedInterfaceFirst = container.get(InterfaceSegregation.Talkable.class);
        Object instanceFromExtendedInterfaceSecond = container.get(InterfaceSegregation.Walkable.class);

        Assert.assertTrue(instanceFromMainInterface instanceof InterfaceSegregation.JohnDoe);
        Assert.assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceFirst);
        Assert.assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceSecond);
    }

    @Test
    public void get_passes_concurrency_test() {
        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.get(OneParamConstructor.class);
                } catch (Exception e) {
                    Assert.fail(e.getMessage());
                }
            }
        };

        runConcurrencyTest(test, 10);
    }

    /**
     * Note: this test is only here to inspect and improve the error output.
     */
    @Test
    public void get_throws_if_deeply_nested_unresolvable_constructor() {
        try {
            container.get(DeeplyNestedUnresolvableConstructor.A.class);
        } catch (ContainerException e) {
            //e.printStackTrace();
        }
    }

}
