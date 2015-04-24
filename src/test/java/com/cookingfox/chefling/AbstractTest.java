package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.exception.TypeNotAllowedException;
import com.cookingfox.chefling.fixtures.*;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * Base test class for {@link Container} tests.
 */
public abstract class AbstractTest {

    protected Container container;

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
    // HELPER VALUES
    //----------------------------------------------------------------------------------------------

    /**
     * Examples of types that are not instantiable.
     */
    protected Class[] notInstantiableTypes = {
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
            Container.class,
            Factory.class,
    };

    /**
     * Returns a map of classes that are not allowed and their instances.
     */
    protected HashMap<Class, Object> getNotAllowedInstances() {
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
        notAllowedInstances.put(Factory.class, new Factory() {
            @Override
            public Object create(ContainerInterface container) throws ContainerException {
                return null;
            }
        });

        // Note: can not use boolean in this context, because it will fail the `instanceof` test
        // notAllowedInstances.put(boolean.class, false);

        return notAllowedInstances;
    }

    /**
     * Returns a map of classes that are not allowed and their sub classes.
     */
    protected HashMap<Class, Class> getNotAllowedSubTypes() {
        HashMap<Class, Class> notAllowedSubTypes = new HashMap<Class, Class>();
        notAllowedSubTypes.put(Object.class, getMock(Object.class).getClass());
        notAllowedSubTypes.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class).getClass());
        notAllowedSubTypes.put(Number.class, Integer.class);
        notAllowedSubTypes.put(PrivateClass.class, getMock(PrivateClass.class).getClass());
        notAllowedSubTypes.put(ProtectedClass.class, getMock(ProtectedClass.class).getClass());
        notAllowedSubTypes.put(MemberClass.class, getMock(MemberClass.class).getClass());
        notAllowedSubTypes.put(ContainerException.class, TypeNotAllowedException.class);
        notAllowedSubTypes.put(Container.class, getMock(Container.class).getClass());
        notAllowedSubTypes.put(ContainerInterface.class, getMock(ContainerInterface.class).getClass());
        notAllowedSubTypes.put(Factory.class, getMock(Factory.class).getClass());

        // Note: can not test the following types, because a mock can not be created
        // notAllowedSubTypes.put(Class.class, getMock(Class.class).getClass());
        // notAllowedSubTypes.put(String.class, getMock(String.class).getClass());
        // notAllowedSubTypes.put(OneValueEnum.class, getMock(OneValueEnum.class).getClass());

        return notAllowedSubTypes;
    }

    //----------------------------------------------------------------------------------------------
    // HELPER INTERNAL CLASSES
    //----------------------------------------------------------------------------------------------

    public class MemberClass {
    }

    private static class PrivateClass {
    }

    protected static class ProtectedClass {
    }

    public static class A {
    }

    public static class B extends A {
    }

    public static class C extends B {
    }

    public static class D extends C {
    }

    public static class E extends D {
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
    protected void runConcurrencyTest(final Runnable test, int numThreads) {
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

    protected Object getMock(Class aClass) {
        return Mockito.mock(aClass);
    }

}
