package com.cookingfox.chefling;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.CheflingFactory;
import com.cookingfox.chefling.api.CheflingLifecycle;
import com.cookingfox.fixtures.chefling._LifecycleWithCallLog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link CheflingLifecycle} callbacks.
 */
public class _LifecycleTest extends AbstractTest {

    @Test
    public void create_type_should_call_lifecycle_create() throws Exception {
        _LifecycleWithCallLog instance = container.createInstance(_LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void create_factory_should_call_lifecycle_create() throws Exception {
        CheflingFactory<_LifecycleWithCallLog> factory = new CheflingFactory<_LifecycleWithCallLog>() {
            @Override
            public _LifecycleWithCallLog createInstance(CheflingContainer container) {
                return new _LifecycleWithCallLog();
            }
        };

        container.mapFactory(_LifecycleWithCallLog.class, factory);

        _LifecycleWithCallLog instance = container.createInstance(_LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void create_instance_should_call_lifecycle_create() throws Exception {
        _LifecycleWithCallLog instance = new _LifecycleWithCallLog();

        container.mapInstance(_LifecycleWithCallLog.class, instance);
        container.createInstance(_LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(0, instance.disposeCalls.size());
    }

    @Test
    public void reset_should_call_lifecycle_destroy() throws Exception {
        _LifecycleWithCallLog instance = container.getInstance(_LifecycleWithCallLog.class);

        container.resetContainer();

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(1, instance.disposeCalls.size());
    }

    @Test
    public void remove_should_call_lifecycle_destroy() throws Exception {
        _LifecycleWithCallLog instance = container.getInstance(_LifecycleWithCallLog.class);

        container.removeInstanceAndMapping(_LifecycleWithCallLog.class);

        assertEquals(1, instance.initializeCalls.size());
        assertEquals(1, instance.disposeCalls.size());
    }

}
