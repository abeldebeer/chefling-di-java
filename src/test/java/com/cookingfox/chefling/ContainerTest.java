package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.LinkedList;
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
        container = null;
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'CREATE' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void create_throws_when_type_not_instantiable() {
        Class[] typesToCheck = joinArrays(notAllowedTypes, notInstantiableTypes);

        for (Class type : typesToCheck) {
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
        MultipleConstructorsTargetAllowed result = container.create(MultipleConstructorsTargetAllowed.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.first);
        Assert.assertNull(result.second);
        Assert.assertNull(result.third);
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
    public void has_returns_false_if_no_value() {
        boolean result = container.has(NoConstructor.class);

        Assert.assertFalse(result);
    }

    @Test
    public void has_returns_true_if_value() throws ContainerException {
        container.set(NoConstructor.class, new NoConstructor());

        boolean result = container.has(NoConstructor.class);

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
        for (Class type : notAllowedTypes) {
            try {
                container.map(MemberClass.class, ExtendedMemberClass.class);

                Assert.fail("Did not receive expected exception for type " + type);
            } catch (ContainerException e) {
                Assert.assertTrue(e instanceof TypeNotAllowedException);
            }
        }
    }

    @Test(expected = TypeNotInstantiableException.class)
    public void map_throws_when_sub_type_not_instantiable() throws ContainerException {
        container.map(NoMethodInterface.class, NoMethodAbstract.class);
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

        Assert.assertEquals(numTests - 1, exceptions.size());
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: 'SET' METHOD
    //----------------------------------------------------------------------------------------------

    @Test
    public void set_throws_when_type_not_allowed() {
        for (Class type : notAllowedTypes) {
            try {
                // note: using base Object class, because it will never reach the "instance of" check
                container.set(type, new Object());

                Assert.fail("Did not get expected exception for type " + type);
            } catch (ContainerException e) {
                Assert.assertTrue(e instanceof TypeNotAllowedException);
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

    @Test
    public void set_overwrites_previous_value() throws ContainerException {
        NoConstructor generated = container.get(NoConstructor.class);
        NoConstructor created = new NoConstructor();

        container.set(NoConstructor.class, created);

        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertSame(created, result);
        Assert.assertNotSame(generated, result);
    }

    @Test
    public void set_accepts_implementation() throws ContainerException {
        NoMethodImplementation instance = new NoMethodImplementation();

        container.set(NoMethodInterface.class, instance);
        container.set(NoMethodAbstract.class, instance);
    }

    //----------------------------------------------------------------------------------------------
    // HELPER VALUES
    //----------------------------------------------------------------------------------------------

    /**
     * Examples of types that are not allowed.
     */
    private Class[] notAllowedTypes = {
            Object.class,
            Class.class,
            String.class,
            ExampleAnnotation.class,
            boolean.class,
            PrivateClass.class,
            ProtectedClass.class,
            MemberClass.class,
            NoValueEnum.class,
    };

    /**
     * Examples of types that are not instantiable.
     */
    private Class[] notInstantiableTypes = {
            NoMethodInterface.class,
            NoMethodAbstract.class,
            PrivateConstructor.class,
    };

    //----------------------------------------------------------------------------------------------
    // HELPER METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Join two arrays.
     */
    public <T> T[] joinArrays(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

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

    public class ExtendedMemberClass extends MemberClass {
    }

    private class PrivateClass {
    }

    protected class ProtectedClass {
    }

}
