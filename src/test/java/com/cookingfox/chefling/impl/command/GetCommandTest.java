package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class GetCommandTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void get_throws_if_type_null() throws Exception {
        container.get(null);
    }

    @Test
    public void get_creates_type_instance() throws Exception {
        NoConstructor result = container.get(NoConstructor.class);

        assertNotNull(result);
    }

    @Test
    public void get_returns_same_instance() throws Exception {
        NoConstructor firstResult = container.get(NoConstructor.class);
        NoConstructor secondResult = container.get(NoConstructor.class);
        NoConstructor thirdResult = container.get(NoConstructor.class);

        assertNotNull(firstResult);
        assertSame(firstResult, secondResult);
        assertSame(firstResult, thirdResult);
        assertSame(secondResult, thirdResult);
    }

    @Test
    public void get_container_returns_self() throws Exception {
        Container byInterface = container.get(Container.class);
        CommandContainer byClass = container.get(CommandContainer.class);

        assertSame(container, byClass);
        assertSame(container, byInterface);
    }

    @Test
    public void get_mapped_type_creates_mapped() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.get(NoMethodInterface.class);

        assertNotNull(result);
        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_self() throws Exception {
        container.get(CircularSelf.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_simple() throws Exception {
        container.get(CircularSimple.A.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void get_detect_circular_complex() throws Exception {
        container.mapType(CircularComplex.CInterface.class, CircularComplex.C.class);
        container.get(CircularComplex.A.class);
    }

    @Test
    public void get_doubly_mapped_type_returns_same_instance() throws Exception {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        Object first = container.get(NoMethodAbstract.class);
        Object second = container.get(NoMethodInterface.class);

        assertSame(first, second);
    }

    @Test
    public void get_quadruply_mapped_type_returns_same_instance() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        Object d = container.get(QuadruplyTyped.D.class);
        Object c = container.get(QuadruplyTyped.C.class);
        Object b = container.get(QuadruplyTyped.B.class);
        Object a = container.get(QuadruplyTyped.A.class);

        assertSame(d, c);
        assertSame(c, b);
        assertSame(b, a);
    }

    @Test
    public void get_multiple_mapped_interfaces_returns_same_instance() throws Exception {
        container.mapType(InterfaceSegregation.Person.class, InterfaceSegregation.JohnDoe.class);
        container.mapType(InterfaceSegregation.Talkable.class, InterfaceSegregation.Person.class);
        container.mapType(InterfaceSegregation.Walkable.class, InterfaceSegregation.Person.class);

        Object instanceFromMainInterface = container.get(InterfaceSegregation.Person.class);
        Object instanceFromExtendedInterfaceFirst = container.get(InterfaceSegregation.Talkable.class);
        Object instanceFromExtendedInterfaceSecond = container.get(InterfaceSegregation.Walkable.class);

        assertTrue(instanceFromMainInterface instanceof InterfaceSegregation.JohnDoe);
        assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceFirst);
        assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceSecond);
    }

    @Test
    public void get_passes_concurrency_test() {
        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.get(OneParamConstructor.class);
                } catch (Exception e) {
                    fail(e.getMessage());
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
