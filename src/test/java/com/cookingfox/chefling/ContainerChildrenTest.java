package com.cookingfox.chefling;

import com.cookingfox.chefling.exception.*;
import com.cookingfox.chefling.fixtures.NoConstructor;
import com.cookingfox.chefling.fixtures.NoMethodImplementation;
import com.cookingfox.chefling.fixtures.NoMethodInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 11/06/15.
 */
public class ContainerChildrenTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TEST CASES: ADD CHILD
    //----------------------------------------------------------------------------------------------

    // TODO: when adding a child, its configuration (mappings & instances) should be checked to prevent clashes with the current Container

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

    @Test(expected = ChildAlreadyAddedException.class)
    public void addChild_throws_if_child_already_added() throws ContainerException {
        Container childContainer = new Container();
        container.addChild(childContainer);
        container.addChild(childContainer);
    }

    @Test(expected = ChildAlreadyAddedException.class)
    public void addChild_throws_if_child_already_added_nested() throws ContainerException {
        Container firstContainer = new Container();
        Container secondContainer = new Container();
        firstContainer.addChild(secondContainer);
        container.addChild(firstContainer);
        container.addChild(secondContainer);
    }

    //----------------------------------------------------------------------------------------------
    // TEST CASES: HAS CHILD
    //----------------------------------------------------------------------------------------------

    @Test
    public void hasChild_returns_false_if_null() {
        boolean result = container.hasChild(null);

        Assert.assertFalse(result);
    }

    @Test
    public void hasChild_returns_false_if_no_child() {
        boolean result = container.hasChild(new Container());

        Assert.assertFalse(result);
    }

    @Test
    public void hasChild_returns_true_if_has_child() throws ContainerException {
        Container childContainer = new Container();
        container.addChild(childContainer);

        boolean result = container.hasChild(childContainer);

        Assert.assertTrue(result);
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
