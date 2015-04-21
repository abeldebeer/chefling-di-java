package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.ContainerException;
import com.cookingfox.chefling.fixtures.LifeCycleWithCallLog;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 21/04/15.
 */
public class LifeCycleTest extends AbstractTest {

    @Test
    public void create_type_calls_lifecycle_create() throws ContainerException {
        LifeCycleWithCallLog instance = container.create(LifeCycleWithCallLog.class);

        Assert.assertTrue(instance.createCalled);
        Assert.assertFalse(instance.destroyCalled);
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

        Assert.assertTrue(instance.createCalled);
        Assert.assertFalse(instance.destroyCalled);
    }

    @Test
    public void reset_removes_mappings_and_instances() throws ContainerException {
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.get(NoMethodInterface.class);

        container.reset();

        Assert.assertFalse(container.has(NoMethodInterface.class));
    }

    @Test
    public void reset_calls_lifecycle_destroy() throws ContainerException {
        LifeCycleWithCallLog instance = container.get(LifeCycleWithCallLog.class);

        container.reset();

        Assert.assertTrue(instance.createCalled);
        Assert.assertTrue(instance.destroyCalled);
    }

    @Test
    public void reset_reinitializes_container() throws ContainerException {
        Assert.assertTrue(container.has(Container.class));

        container.reset();

        Assert.assertTrue(container.has(Container.class));
    }

}
