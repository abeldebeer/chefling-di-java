package com.cookingfox.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.fixtures.chefling.LifecycleWithCallLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link CheflingLifecycle} callbacks.
 */
public class LifecycleTest extends AbstractTest {

    @Test
    public void create_type_should_call_lifecycle_create() throws Exception {
        LifecycleWithCallLog instance = container.createInstance(LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void create_factory_should_call_lifecycle_create() throws Exception {
        CheflingFactory<LifecycleWithCallLog> factory = new CheflingFactory<LifecycleWithCallLog>() {
            @Override
            public LifecycleWithCallLog createInstance(CheflingContainer container) {
                return new LifecycleWithCallLog();
            }
        };

        container.mapFactory(LifecycleWithCallLog.class, factory);

        LifecycleWithCallLog instance = container.createInstance(LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void create_instance_should_call_lifecycle_create() throws Exception {
        LifecycleWithCallLog instance = new LifecycleWithCallLog();

        container.mapInstance(LifecycleWithCallLog.class, instance);
        container.createInstance(LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void reset_should_call_lifecycle_destroy() throws Exception {
        LifecycleWithCallLog instance = container.getInstance(LifecycleWithCallLog.class);

        container.resetContainer();

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(1, instance.disposeCalls.size());
    }

    @Test
    public void remove_should_call_lifecycle_destroy() throws Exception {
        LifecycleWithCallLog instance = container.getInstance(LifecycleWithCallLog.class);

        container.removeInstanceAndMapping(LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(1, instance.disposeCalls.size());
    }

}
