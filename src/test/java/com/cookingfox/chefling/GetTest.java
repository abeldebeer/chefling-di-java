package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.fixtures.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Container#get(Class)}.
 */
public class GetTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

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
        container.mapType(D.class, E.class);
        container.mapType(C.class, D.class);
        container.mapType(B.class, C.class);
        container.mapType(A.class, B.class);

        Object d = container.get(D.class);
        Object c = container.get(C.class);
        Object b = container.get(B.class);
        Object a = container.get(A.class);

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

}
