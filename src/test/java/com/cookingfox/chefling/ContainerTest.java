package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Tests all container functionality.
 */
public class ContainerTest {

    private Container container;

    //----------------------------------------------------------------------------------------------
    // TEST LIFECYCLE
    //----------------------------------------------------------------------------------------------

    @Before
    public void setUp() throws Exception {
        container = new Container();
    }

    @After
    public void tearDown() throws Exception {
        Assert.assertEquals(0, container.currentlyResolving.size());

        container = null;
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'CREATE' METHOD
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
        container.map(NoMethodInterface.class, NoMethodImplementation.class);

        MultipleConstructorsTargetAllowed result = container.create(MultipleConstructorsTargetAllowed.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.first);
        Assert.assertNotNull(result.second);
        Assert.assertNull(result.third);
        Assert.assertNull(result.fourth);
    }

    @Test
    public void create_should_use_subtype_mapping() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodImplementation.class);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        Assert.assertTrue(result instanceof NoMethodImplementation);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'GET' METHOD
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
        container.map(NoMethodInterface.class, NoMethodImplementation.class);

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
        container.map(CircularComplex.CInterface.class, CircularComplex.C.class);
        container.get(CircularComplex.A.class);
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

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'HAS' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void has_returns_false_if_no_instance_or_mapping() {
        boolean result = container.has(NoMethodInterface.class);

        Assert.assertFalse(result);
    }

    @Test
    public void has_returns_true_if_instance() throws ContainerException {
        container.set(NoMethodInterface.class, new NoMethodImplementation());

        boolean result = container.has(NoMethodInterface.class);

        Assert.assertTrue(result);
    }

    @Test
    public void has_returns_true_if_mapping() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodImplementation.class);

        boolean result = container.has(NoMethodInterface.class);

        Assert.assertTrue(result);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'MAP' METHOD
    //----------------------------------------------------------------------------------------------

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void map_throws_when_mapping_exists() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodImplementation.class);
        container.map(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void map_throws_when_sub_type_same_as_type() throws ContainerException {
        container.map(NoMethodImplementation.class, NoMethodImplementation.class);
    }

    @Test(expected = NotASubTypeException.class)
    public void map_throws_when_sub_type_not_extends_type() throws ContainerException {
        Class type = NoConstructor.class;
        Class subType = Object.class;

        container.map(type, subType);
    }

    @Test
    public void map_throws_when_base_type_not_allowed() throws ContainerException {
        for (Map.Entry<Class, Class> entry : getNotAllowedSubTypes().entrySet()) {
            try {
                container.map(entry.getKey(), entry.getValue());

                Assert.fail("Did not receive expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = TypeNotInstantiableException.class)
    public void map_throws_when_sub_type_not_instantiable() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodAbstract.class);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void map_first_set_then_map_throws() throws ContainerException {
        container.set(NoMethodInterface.class, new NoMethodImplementation());
        container.map(NoMethodInterface.class, NoMethodImplementation.class);
    }

    @Test
    public void map_passes_concurrency_test() {
        int numTests = 10;
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();

        Runnable test = new Runnable() {
            @Override
            public void run() {
                try {
                    container.map(NoMethodInterface.class, NoMethodImplementation.class);
                } catch (ContainerException e) {
                    exceptions.add(e);
                }
            }
        };

        runConcurrencyTest(test, numTests);

        Assert.assertEquals("Expected number of exceptions", numTests - 1, exceptions.size());
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'SET' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void set_throws_when_type_not_allowed() {
        for (Map.Entry<Class, Object> entry : getNotAllowedInstances().entrySet()) {
            try {
                container.set(entry.getKey(), entry.getValue());

                Assert.fail("Did not get expected exception for type " + entry.getKey());
            } catch (ContainerException e) {
                Assert.assertTrue(e.getMessage(), e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = NotAnInstanceOfTypeException.class)
    public void set_throws_if_instance_not_instanceof_type() throws ContainerException {
        // use variable to prevent generic warning
        Class type = NoConstructor.class;

        container.set(type, new Object());
    }

    @Test
    public void set_stores_instance() throws ContainerException {
        NoConstructor instance = new NoConstructor();

        container.set(NoConstructor.class, instance);

        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertSame(instance, result);
    }

    @Test(expected = ReplaceInstanceNotAllowedException.class)
    public void set_throws_if_stored_instance_default() throws ContainerException {
        container.set(NoConstructor.class, new NoConstructor());
        container.set(NoConstructor.class, new NoConstructor());
    }

    @Test
    public void set_accepts_replace_stored_instance_if_allowed() throws ContainerException {
        container.set(NoConstructor.class, new NoConstructor());
        container.set(NoConstructor.class, new NoConstructor(), true);
    }

    @Test
    public void set_accepts_implementation() throws ContainerException {
        NoMethodImplementation instance = new NoMethodImplementation();

        container.set(NoMethodInterface.class, instance);
        container.set(NoMethodAbstract.class, instance);
    }

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void set_first_map_then_set_throws() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodImplementation.class);
        container.set(NoMethodInterface.class, new NoMethodImplementation());
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'GET DEFAULT' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void get_default_concurrent_uses_same_instance() {
        final Container defaultContainer = Container.getDefault();

        Runnable test = new Runnable() {
            public void run() {
                Assert.assertSame(defaultContainer, Container.getDefault());
            }
        };

        runConcurrencyTest(test, 5);
    }

    //----------------------------------------------------------------------------------------------
    // HELPER VALUES
    //----------------------------------------------------------------------------------------------

    /**
     * Examples of types that are not instantiable.
     */
    private Class[] notInstantiableTypes = {
            Object.class,
            Class.class,
            String.class,
            ExampleAnnotation.class,
            boolean.class,
            PrivateClass.class,
            ProtectedClass.class,
            MemberClass.class,
            OneValueEnum.class,
            ContainerException.class,
            NoMethodInterface.class,
            NoMethodAbstract.class,
            PrivateConstructor.class,
    };

    /**
     * Returns a map of classes that are not allowed and their instances.
     */
    private HashMap<Class, Object> getNotAllowedInstances() {
        HashMap<Class, Object> notAllowedInstances = new HashMap<Class, Object>();
        notAllowedInstances.put(Object.class, new Object());
        notAllowedInstances.put(Class.class, Object.class);
        notAllowedInstances.put(String.class, "");
        notAllowedInstances.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class));
        notAllowedInstances.put(PrivateClass.class, new PrivateClass());
        notAllowedInstances.put(ProtectedClass.class, new ProtectedClass());
        notAllowedInstances.put(MemberClass.class, new MemberClass());
        notAllowedInstances.put(OneValueEnum.class, OneValueEnum.VALUE);
        notAllowedInstances.put(ContainerException.class, new ContainerException(""));
        notAllowedInstances.put(Container.class, new Container());
        notAllowedInstances.put(ContainerInterface.class, new Container());

        // Note: can not use boolean in this context, because it will fail the `instanceof` test
        // notAllowedInstances.put(boolean.class, false);

        return notAllowedInstances;
    }

    /**
     * Returns a map of classes that are not allowed and their sub classes.
     */
    private HashMap<Class, Class> getNotAllowedSubTypes() {
        HashMap<Class, Class> notAllowedSubTypes = new HashMap<Class, Class>();
        notAllowedSubTypes.put(Object.class, getMock(Object.class).getClass());
        notAllowedSubTypes.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class).getClass());
        notAllowedSubTypes.put(Number.class, Integer.class);
        notAllowedSubTypes.put(PrivateClass.class, getMock(PrivateClass.class).getClass());
        notAllowedSubTypes.put(ProtectedClass.class, getMock(ProtectedClass.class).getClass());
        notAllowedSubTypes.put(MemberClass.class, getMock(MemberClass.class).getClass());
        notAllowedSubTypes.put(ContainerException.class, TypeNotAllowedException.class);

        // Note: can not test the following types, because a mock can not be created
        // notAllowedSubTypes.put(Class.class, getMock(Class.class).getClass());
        // notAllowedSubTypes.put(String.class, getMock(String.class).getClass());
        // notAllowedSubTypes.put(OneValueEnum.class, getMock(OneValueEnum.class).getClass());

        return notAllowedSubTypes;
    }

    private Object getMock(Class aClass) {
        return Mockito.mock(aClass);
    }

    //----------------------------------------------------------------------------------------------
    // HELPER METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Run a concurrency test on a specified number of threads.
     *
     * @param test       The test to execute
     * @param numThreads The number of threads to run this test on, concurrently
     */
    private void runConcurrencyTest(final Runnable test, int numThreads) {
        final CountDownLatch latch = new CountDownLatch(1);

        Runnable testWrapper = new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    test.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        LinkedList<Thread> threads = new LinkedList<Thread>();

        // create threads
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Thread(testWrapper));
        }

        // start threads
        for (Thread thread : threads) {
            thread.start();
        }

        latch.countDown();

        // wait for threads to end
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // HELPER INTERNAL CLASSES
    //----------------------------------------------------------------------------------------------

    public class MemberClass {
    }

    private class PrivateClass {
    }

    protected class ProtectedClass {
    }

}
