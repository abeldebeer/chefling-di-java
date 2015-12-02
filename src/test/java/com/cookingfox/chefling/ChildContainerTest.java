package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for Container children support.
 */
public class ChildContainerTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES: ADD CHILD
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullValueNotAllowedException.class)
    public void addChild_throws_if_child_null() throws ContainerException {
        container.addChild(null);
    }

    @Test(expected = ChildCannotBeSelfException.class)
    public void addChild_throws_if_child_self() throws ContainerException {
        container.addChild(container);
    }

    @Test(expected = ChildCannotBeDefaultException.class)
    public void addChild_throws_if_child_default() throws ContainerException {
        Container defaultContainer = Container.getDefault();
        Container childContainer = new Container();
        childContainer.addChild(defaultContainer);
    }

    @Test(expected = ContainerAlreadyAddedToSetException.class)
    public void addChild_throws_if_child_already_added() throws ContainerException {
        Container childContainer = new Container();
        container.addChild(childContainer);
        container.addChild(childContainer);
    }

    @Test(expected = ContainerAlreadyAddedToSetException.class)
    public void addChild_throws_if_child_already_added_nested() throws ContainerException {
        Container firstContainer = new Container();
        Container secondContainer = new Container();
        firstContainer.addChild(secondContainer);
        container.addChild(firstContainer);
        container.addChild(secondContainer);
    }

    @Test(expected = ChildConfigurationConflictException.class)
    public void addChild_throws_if_child_configuration_clashes() throws ContainerException {
        Container childContainer = new Container();
        childContainer.get(NoConstructor.class);
        container.get(NoConstructor.class);
        container.addChild(childContainer);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: CREATE
    //----------------------------------------------------------------------------------------------

    @Test
    public void create_uses_children() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.addChild(childContainer);

        NoMethodInterface result = container.create(NoMethodInterface.class);

        Assert.assertNotNull(result);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: GET
    //----------------------------------------------------------------------------------------------

    @Test
    public void get_previous_instance_uses_children() throws ContainerException {
        Container childContainer = new Container();
        NoConstructor instance = childContainer.get(NoConstructor.class);
        container.addChild(childContainer);

        NoConstructor result = container.get(NoConstructor.class);

        Assert.assertSame(instance, result);
    }

    @Test
    public void get_mapping_uses_children() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        container.addChild(childContainer);

        NoMethodInterface result = container.get(NoMethodInterface.class);

        Assert.assertTrue(result instanceof NoMethodImplementation);
    }

    @Test
    public void get_checks_current_mappings_before_checking_children() throws Exception {
        final Container mainContainer = new Container();

        mainContainer.mapInstance(NoMethodInterface.class, new NoMethodImplementation());

        Container childContainer = new Container();

        mainContainer.addChild(childContainer);

        childContainer.mapFactory(NoMethodInterface.class, new Factory<NoMethodInterface>() {
            @Override
            public NoMethodInterface create(ContainerInterface container) throws ContainerException {
                return mainContainer.get(NoMethodInterface.class);
            }
        });

        NoMethodInterface result = childContainer.get(NoMethodInterface.class);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: HAS
    //----------------------------------------------------------------------------------------------

    @Test
    public void has_returns_true_if_child_has() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(childContainer);

        boolean result = container.has(NoConstructor.class);

        Assert.assertTrue(result);
    }

    @Test
    public void has_returns_true_if_nested_child_has() throws ContainerException {
        Container firstContainer = new Container();
        Container secondContainer = new Container();
        secondContainer.mapInstance(NoConstructor.class, new NoConstructor());
        firstContainer.addChild(secondContainer);
        container.addChild(firstContainer);

        boolean result = container.has(NoConstructor.class);

        Assert.assertTrue(result);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: MAP FACTORY
    //----------------------------------------------------------------------------------------------

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapFactory_throws_if_mapped_in_child() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(childContainer);

        container.mapFactory(NoConstructor.class, new Factory<NoConstructor>() {
            @Override
            public NoConstructor create(ContainerInterface container) throws ContainerException {
                return new NoConstructor();
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: MAP INSTANCE
    //----------------------------------------------------------------------------------------------

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapInstance_throws_if_mapped_in_child() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(childContainer);
        container.mapInstance(NoConstructor.class, new NoConstructor());
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: MAP TYPE
    //----------------------------------------------------------------------------------------------

    @Test(expected = TypeMappingAlreadyExistsException.class)
    public void mapType_throws_if_mapped_in_child() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoMethodInterface.class, new NoMethodImplementation());
        container.addChild(childContainer);
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: REMOVE
    //----------------------------------------------------------------------------------------------

    @Test
    public void remove_also_removes_from_children() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(childContainer);
        container.remove(NoConstructor.class);

        boolean result = childContainer.has(NoConstructor.class);

        Assert.assertFalse(result);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: RESET
    //----------------------------------------------------------------------------------------------

    @Test
    public void reset_also_resets_children() throws ContainerException {
        Container childContainer = new Container();
        childContainer.mapInstance(NoConstructor.class, new NoConstructor());
        container.addChild(childContainer);
        container.reset();

        boolean result = childContainer.has(NoConstructor.class);

        Assert.assertFalse(result);
    }

}
