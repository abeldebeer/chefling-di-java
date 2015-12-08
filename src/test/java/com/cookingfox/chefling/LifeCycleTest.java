package com.cookingfox.chefling;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.Factory;
import com.cookingfox.chefling.api.LifeCycle;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.LifeCycleWithCallLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link LifeCycle} callbacks.
 */
public class LifeCycleTest extends AbstractTest {

    @Test
    public void create_type_should_call_lifecycle_create() throws Exception {
        LifeCycleWithCallLog instance = container.create(LifeCycleWithCallLog.class);

        assertEquals(1, instance.onCreateCalls.size());
        assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void create_factory_should_call_lifecycle_create() throws Exception {
        Factory<LifeCycleWithCallLog> factory = new Factory<LifeCycleWithCallLog>() {
            @Override
            public LifeCycleWithCallLog create(Container container) throws ContainerException {
                return new LifeCycleWithCallLog();
            }
        };

        container.mapFactory(LifeCycleWithCallLog.class, factory);

        LifeCycleWithCallLog instance = container.create(LifeCycleWithCallLog.class);

        assertEquals(1, instance.onCreateCalls.size());
        assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void create_instance_should_call_lifecycle_create() throws Exception {
        LifeCycleWithCallLog instance = new LifeCycleWithCallLog();

        container.mapInstance(LifeCycleWithCallLog.class, instance);
        container.create(LifeCycleWithCallLog.class);

        assertEquals(1, instance.onCreateCalls.size());
        assertEquals(0, instance.onDestroyCalls.size());
    }

    @Test
    public void reset_should_call_lifecycle_destroy() throws Exception {
        LifeCycleWithCallLog instance = container.get(LifeCycleWithCallLog.class);

        container.reset();

        assertEquals(1, instance.onCreateCalls.size());
        assertEquals(1, instance.onDestroyCalls.size());
    }

    @Test
    public void remove_should_call_lifecycle_destroy() throws Exception {
        LifeCycleWithCallLog instance = container.get(LifeCycleWithCallLog.class);

        container.remove(LifeCycleWithCallLog.class);

        assertEquals(1, instance.onCreateCalls.size());
        assertEquals(1, instance.onDestroyCalls.size());
    }

}
