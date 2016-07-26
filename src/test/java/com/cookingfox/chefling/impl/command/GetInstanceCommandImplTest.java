package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.exception.CircularDependencyDetectedException;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.NullValueNotAllowedException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link GetInstanceCommandImpl}.
 */
public class GetInstanceCommandImplTest extends AbstractTest {

    @Test(expected = NullValueNotAllowedException.class)
    public void should_throw_if_type_null() throws Exception {
        container.getInstance(null);
    }

    @Test
    public void should_create_type_instance() throws Exception {
        NoConstructor result = container.getInstance(NoConstructor.class);

        assertNotNull(result);
    }

    @Test
    public void should_return_same_instance() throws Exception {
        NoConstructor firstResult = container.getInstance(NoConstructor.class);
        NoConstructor secondResult = container.getInstance(NoConstructor.class);
        NoConstructor thirdResult = container.getInstance(NoConstructor.class);

        assertNotNull(firstResult);
        assertSame(firstResult, secondResult);
        assertSame(firstResult, thirdResult);
        assertSame(secondResult, thirdResult);
    }

    @Test
    public void container_should_return_self() throws Exception {
        CheflingContainer byInterface = container.getInstance(CheflingContainer.class);
        CommandContainer byClass = container.getInstance(CommandContainer.class);

        assertSame(container, byClass);
        assertSame(container, byInterface);
    }

    @Test
    public void mapped_type_should_create_mapped() throws Exception {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertNotNull(result);
        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void should_detect_circular_dependency_self() throws Exception {
        container.getInstance(CircularSelf.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void should_detect_circular_dependency_simple() throws Exception {
        container.getInstance(CircularSimple.A.class);
    }

    @Test(expected = CircularDependencyDetectedException.class)
    public void should_detect_circular_dependency_complex() throws Exception {
        container.mapType(CircularComplex.CInterface.class, CircularComplex.C.class);
        container.getInstance(CircularComplex.A.class);
    }

    @Test
    public void doubly_mapped_type_should_return_same_instance() throws Exception {
        container.mapType(NoMethodAbstract.class, NoMethodImplementation.class);
        container.mapType(NoMethodInterface.class, NoMethodAbstract.class);

        Object first = container.getInstance(NoMethodAbstract.class);
        Object second = container.getInstance(NoMethodInterface.class);

        assertSame(first, second);
    }

    @Test
    public void quadruply_mapped_type_should_return_same_instance() throws Exception {
        container.mapType(QuadruplyTyped.D.class, QuadruplyTyped.E.class);
        container.mapType(QuadruplyTyped.C.class, QuadruplyTyped.D.class);
        container.mapType(QuadruplyTyped.B.class, QuadruplyTyped.C.class);
        container.mapType(QuadruplyTyped.A.class, QuadruplyTyped.B.class);

        Object d = container.getInstance(QuadruplyTyped.D.class);
        Object c = container.getInstance(QuadruplyTyped.C.class);
        Object b = container.getInstance(QuadruplyTyped.B.class);
        Object a = container.getInstance(QuadruplyTyped.A.class);

        assertSame(d, c);
        assertSame(c, b);
        assertSame(b, a);
    }

    @Test
    public void multiple_mapped_interfaces_should_return_same_instance() throws Exception {
        container.mapType(InterfaceSegregation.Person.class, InterfaceSegregation.JohnDoe.class);
        container.mapType(InterfaceSegregation.Talkable.class, InterfaceSegregation.Person.class);
        container.mapType(InterfaceSegregation.Walkable.class, InterfaceSegregation.Person.class);

        Object instanceFromMainInterface = container.getInstance(InterfaceSegregation.Person.class);
        Object instanceFromExtendedInterfaceFirst = container.getInstance(InterfaceSegregation.Talkable.class);
        Object instanceFromExtendedInterfaceSecond = container.getInstance(InterfaceSegregation.Walkable.class);

        assertTrue(instanceFromMainInterface instanceof InterfaceSegregation.JohnDoe);
        assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceFirst);
        assertSame(instanceFromMainInterface, instanceFromExtendedInterfaceSecond);
    }

    @Test
    public void should_return_instance_of_parent() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        parentContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        NoMethodInterface instance = parentContainer.getInstance(NoMethodImplementation.class);
        container.setParentContainer(parentContainer);
        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertSame(instance, result);
    }

    @Test
    public void should_return_mapped_instance_of_parent_from_factory() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        final NoMethodInterface instance = new NoMethodImplementation();
        parentContainer.mapFactory(NoMethodInterface.class, new CheflingFactory<NoMethodInterface>() {
            @Override
            public NoMethodInterface createInstance(CheflingContainer container) {
                return instance;
            }
        });
        container.setParentContainer(parentContainer);
        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertSame(instance, result);
    }

    @Test
    public void should_return_mapped_instance_of_parent() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        NoMethodInterface instance = new NoMethodImplementation();
        parentContainer.mapInstance(NoMethodInterface.class, instance);
        container.setParentContainer(parentContainer);
        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertSame(instance, result);
    }

    @Test
    public void should_return_mapped_type_of_parent() throws Exception {
        CheflingContainer parentContainer = new CommandContainer();
        parentContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.setParentContainer(parentContainer);
        NoMethodInterface result = container.getInstance(NoMethodInterface.class);

        assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void should_call_lifecycle_initialize_of_mapped_instance() throws Exception {
        LifecycleWithCallLog lifecycleWithCallLog = new LifecycleWithCallLog();

        container.mapInstance(LifecycleWithCallLog.class, lifecycleWithCallLog);

        LifecycleWithCallLog result = container.getInstance(LifecycleWithCallLog.class);

        assertSame(lifecycleWithCallLog, result);
        assertTrue(lifecycleWithCallLog.initializeCalls.size() == 1);
    }

    @Test
    public void should_call_lifecycle_initialize_of_saved_instance_only_once() throws Exception {
        // call a few times
        container.getInstance(LifecycleWithCallLog.class);
        container.getInstance(LifecycleWithCallLog.class);
        LifecycleWithCallLog result = container.getInstance(LifecycleWithCallLog.class);

        assertTrue(result.initializeCalls.size() == 1);
    }

    @Test
    public void should_pass_concurrency_test() {
        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.getInstance(OneParamConstructor.class);
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
    public void should_throw_if_deeply_nested_unresolvable_constructor() {
        try {
            container.getInstance(DeeplyNestedUnresolvableConstructor.A.class);
        } catch (ContainerException e) {
            // e.printStackTrace();
        }
    }

}
