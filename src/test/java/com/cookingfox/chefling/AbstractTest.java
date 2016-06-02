package com.cookingfox.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.TypeNotAllowedException;
import com.cookingfox.chefling.impl.command.CommandContainer;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Before;
import org.mockito.Mockito;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        container = new CommandContainer();
    }

    //----------------------------------------------------------------------------------------------
    // HELPERS
    //----------------------------------------------------------------------------------------------

    /**
     * Examples of types that are not instantiable.
     */
    protected final Class[] notInstantiableTypes = {
            Object.class,
            Class.class,
            String.class,
            HashMap.class,
            SimpleJavaFileObject.class,
            ExampleAnnotation.class,
            boolean.class,
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
     * Returns a map of classes that are not allowed and their instances.
     */
    protected HashMap<Class, Object> getNotAllowedInstances() {
        HashMap<Class, Object> notAllowedInstances = new HashMap<>();
        notAllowedInstances.put(Object.class, new Object());
        notAllowedInstances.put(Class.class, Object.class);
        notAllowedInstances.put(String.class, "");
        notAllowedInstances.put(HashMap.class, getMock(HashMap.class));
        notAllowedInstances.put(SimpleJavaFileObject.class, getMock(SimpleJavaFileObject.class));
        notAllowedInstances.put(ExampleAnnotation.class, getMock(ExampleAnnotation.class));
        notAllowedInstances.put(NonPublicClasses.getPrivateClass(), getMock(NonPublicClasses.getPrivateClass()));
        notAllowedInstances.put(NonPublicClasses.getProtectedClass(), getMock(NonPublicClasses.getProtectedClass()));
        notAllowedInstances.put(HasMember.getMemberClass(), getMock(HasMember.getMemberClass()));
        notAllowedInstances.put(OneValueEnum.class, OneValueEnum.VALUE);
        notAllowedInstances.put(Throwable.class, new Throwable());
        notAllowedInstances.put(ContainerException.class, new ContainerException(""));
        notAllowedInstances.put(CheflingContainer.class, new CommandContainer());
        notAllowedInstances.put(CheflingFactory.class, getMock(CheflingFactory.class));
        notAllowedInstances.put(CheflingLifecycle.class, getMock(CheflingLifecycle.class));

        // Note: can not use boolean in this context, because it will fail the `instanceof` test
        // notAllowedInstances.put(boolean.class, false);

        return notAllowedInstances;
    }

    /**
     * Returns a map of classes that are not allowed and their sub classes.
     */
    protected HashMap<Class, Class> getNotAllowedSubTypes() {
        HashMap<Class, Class> notAllowedSubTypes = new HashMap<>();
        notAllowedSubTypes.put(Object.class, getMock(Object.class).getClass());
        notAllowedSubTypes.put(HashMap.class, LinkedHashMap.class);
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

        return notAllowedSubTypes;
    }

    protected Object getMock(Class aClass) {
        return Mockito.mock(aClass);
    }

    /**
     * Run a concurrency test on a specified number of threads.
     *
     * @param test       The test to execute
     * @param numThreads The number of threads to run this test on, concurrently
     */
    protected void runConcurrencyTest(final Runnable test, int numThreads) {
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
