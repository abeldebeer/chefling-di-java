package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.fixtures.LifeCycleWithCallLog;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link LifeCycle} integration in the Container.
 */
public class LifeCycleTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES
    //----------------------------------------------------------------------------------------------

    @Test
    public void create_type_calls_lifecycle_create() throws ContainerException {
        LifeCycleWithCallLog instance = container.create(LifeCycleWithCallLog.class);

        Assert.assertEquals(1, instance.onCreateCalls.size());
        Assert.assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void create_factory_calls_lifecycle_create() throws ContainerException {
        Factory<LifeCycleWithCallLog> factory = new Factory<LifeCycleWithCallLog>() {
            @Override
            public LifeCycleWithCallLog create(ContainerInterface container) throws ContainerException {
                return new LifeCycleWithCallLog();
            }
        };

        container.mapFactory(LifeCycleWithCallLog.class, factory);

        LifeCycleWithCallLog instance = container.create(LifeCycleWithCallLog.class);

        Assert.assertEquals(1, instance.onCreateCalls.size());
        Assert.assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void create_instance_calls_lifecycle_create() throws ContainerException {
        LifeCycleWithCallLog instance = new LifeCycleWithCallLog();

        container.mapInstance(LifeCycleWithCallLog.class, instance);
        container.create(LifeCycleWithCallLog.class);

        Assert.assertEquals(1, instance.onCreateCalls.size());
        Assert.assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void reset_calls_lifecycle_destroy() throws ContainerException {
        LifeCycleWithCallLog instance = container.get(LifeCycleWithCallLog.class);

        container.reset();

        Assert.assertEquals(1, instance.onCreateCalls.size());
        Assert.assertEquals(1, instance.onDestroyCalls.size());
    }

    @Test
    public void remove_calls_lifecycle_destroy() throws ContainerException {
        LifeCycleWithCallLog instance = container.get(LifeCycleWithCallLog.class);

        container.remove(LifeCycleWithCallLog.class);

        Assert.assertEquals(1, instance.onCreateCalls.size());
        Assert.assertEquals(1, instance.onDestroyCalls.size());
    }

}
