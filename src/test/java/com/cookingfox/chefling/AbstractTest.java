package com.cookingfox.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.TypeNotAllowedException;
import com.cookingfox.chefling.impl.command.CommandContainer;
import com.cookingfox.chefling.impl.command.CommandContainerTestHelper;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Before;
import org.mockito.Mockito;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Base class for unit tests.
 */
public abstract class AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TESTS SETUP
    //----------------------------------------------------------------------------------------------

    protected CommandContainer container;

    @Before
    public void setUp() throws Exception {
        // clear static type cache
        CommandContainerTestHelper.CLEAR_TYPE_CACHE();

        container = new CommandContainer();
    }

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    /**
     * Examples of types that are not instantiable.
     */
    protected static final Class[] NOT_INSTANTIABLE_TYPES = {
            Object.class,
            Class.class,
            String.class,
            Map.class,
            SimpleJavaFileObject.class,
            ExampleAnnotation.class,
            boolean.class,
            NonPublicClasses.getPackageLevelClass(),
            NonPublicClasses.getPrivateClass(),
            NonPublicClasses.getProtectedClass(),
            HasMember.getMemberClass(),
            OneValueEnum.class,
            Throwable.class,
            ContainerException.class,
            NoMethodInterface.class,
            NoMethodAbstract.class,
            PrivateConstructor.class,
            CheflingContainer.class,
            CheflingFactory.class,
            CheflingLifecycle.class,
    };

    /**
     * Examples of types that are not allowed, with instances.
     */
    protected final static Map<Class, Object> NOT_ALLOWED_INSTANCES;

    /**
     * Examples of types that are not allowed, with sub types.
     */
    protected final static Map<Class, Class> NOT_ALLOWED_SUB_TYPES;

    static {
        /* NOT ALLOWED INSTANCES */

        Map<Class, Object> notAllowedInstances = new LinkedHashMap<>();
        notAllowedInstances.put(Object.class, new Object());
        notAllowedInstances.put(Class.class, Object.class);
        notAllowedInstances.put(String.class, "");
        notAllowedInstances.put(Map.class, getMock(Map.class));
        notAllowedInstances.put(SimpleJavaFileObject.class, getMock(SimpleJavaFileObject.class));
        notAllowedInstances.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class));
        notAllowedInstances.put(NonPublicClasses.getPackageLevelClass(), getMock(NonPublicClasses.getPackageLevelClass()));
        notAllowedInstances.put(NonPublicClasses.getPrivateClass(), getMock(NonPublicClasses.getPrivateClass()));
        notAllowedInstances.put(NonPublicClasses.getProtectedClass(), getMock(NonPublicClasses.getProtectedClass()));
        notAllowedInstances.put(HasMember.getMemberClass(), getMock(HasMember.getMemberClass()));
        notAllowedInstances.put(OneValueEnum.class, OneValueEnum.VALUE);
        notAllowedInstances.put(Throwable.class, new Throwable("example"));
        notAllowedInstances.put(ContainerException.class, new ContainerException("example"));
        notAllowedInstances.put(CheflingContainer.class, new CommandContainer());
        notAllowedInstances.put(CheflingFactory.class, getMock(CheflingFactory.class));
        notAllowedInstances.put(CheflingLifecycle.class, getMock(CheflingLifecycle.class));

        // Note: can not use boolean in this context, because it will fail the `instanceof` test
        // notAllowedInstances.put(boolean.class, false);

        NOT_ALLOWED_INSTANCES = Collections.unmodifiableMap(notAllowedInstances);

        /* NOT ALLOWED SUB TYPES */

        Map<Class, Class> notAllowedSubTypes = new LinkedHashMap<>();
        notAllowedSubTypes.put(Object.class, getMock(Object.class).getClass());
        notAllowedSubTypes.put(Map.class, LinkedHashMap.class);
        notAllowedSubTypes.put(JavaFileObject.class, SimpleJavaFileObject.class);
        notAllowedSubTypes.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class).getClass());
        notAllowedSubTypes.put(Number.class, Integer.class);
        notAllowedSubTypes.put(NonPublicClasses.getPrivateClass(), getMock(NonPublicClasses.getPrivateClass()).getClass());
        notAllowedSubTypes.put(NonPublicClasses.getProtectedClass(), getMock(NonPublicClasses.getProtectedClass()).getClass());
        notAllowedSubTypes.put(HasMember.getMemberClass(), getMock(HasMember.getMemberClass()).getClass());
        notAllowedSubTypes.put(Throwable.class, Exception.class);
        notAllowedSubTypes.put(ContainerException.class, TypeNotAllowedException.class);
        notAllowedSubTypes.put(CheflingContainer.class, getMock(CheflingContainer.class).getClass());
        notAllowedSubTypes.put(CheflingFactory.class, getMock(CheflingFactory.class).getClass());
        notAllowedSubTypes.put(CheflingLifecycle.class, getMock(CheflingLifecycle.class).getClass());

        // Note: can not test the following types, because a mock can not be created
        // notAllowedSubTypes.put(Class.class, getMock(Class.class).getClass());
        // notAllowedSubTypes.put(String.class, getMock(String.class).getClass());
        // notAllowedSubTypes.put(OneValueEnum.class, getMock(OneValueEnum.class).getClass());

        NOT_ALLOWED_SUB_TYPES = Collections.unmodifiableMap(notAllowedSubTypes);
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    /**
     * Creates a mock implementation of the provided class.
     *
     * @param aClass The class to mock.
     * @param <T>    Indicates the concrete type.
     * @return A mock implementation of the provided class.
     */
    protected static <T> T getMock(Class<T> aClass) {
        return Mockito.mock(aClass);
    }

    /**
     * Run a concurrency test on a specified number of threads.
     *
     * @param test       The test to execute
     * @param numThreads The number of threads to run this test on, concurrently
     */
    protected static void runConcurrencyTest(final Runnable test, int numThreads) {
        final CountDownLatch latch = new CountDownLatch(1);

        Runnable testWrapper = new Runnable() {
            public void run() {
                try {
                    latch.await();
                    test.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Thread[] threads = new Thread[numThreads];

        // create and start threads
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(testWrapper);
            thread.start();
            threads[i] = thread;
        }

        // run all tests concurrently
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

}
