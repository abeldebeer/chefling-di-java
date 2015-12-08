package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.impl.helper.Applier;
import com.cookingfox.chefling.impl.helper.Matcher;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodAbstract;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link AbstractCommand}.
 */
public class AbstractCommandTest extends AbstractTest {
    
    //----------------------------------------------------------------------------------------------
    // SETUP
    //----------------------------------------------------------------------------------------------
    
    AbstractCommand command;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        command = new AddChildCommand(container);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: APPLY ALL
    //----------------------------------------------------------------------------------------------

    @Test
    public void applyAll_should_call_apply_for_all_parents_and_children() throws Exception {
        CommandContainer parentA = new CommandContainer();
        CommandContainer parentB = new CommandContainer();
        CommandContainer childA = new CommandContainer();
        CommandContainer childB = new CommandContainer();

        parentA.setParent(parentB);
        container.setParent(parentA);
        container.addChild(childA);
        childA.addChild(childB);

        final Set<CommandContainer> called = new LinkedHashSet<>();
        final AtomicInteger counter = new AtomicInteger(0);

        command.applyAll(container, new Applier() {
            @Override
            public void apply(CommandContainer container) {
                called.add(container);
                counter.incrementAndGet();
            }
        });

        assertTrue(called.contains(parentB));
        assertTrue(called.contains(parentA));
        assertTrue(called.contains(container));
        assertTrue(called.contains(childA));
        assertTrue(called.contains(childB));

        assertEquals(5, counter.get());
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: COMPILE TYPES
    //----------------------------------------------------------------------------------------------

    @Test
    public void compileTypes_should_return_empty_if_no_instances_or_mappings() throws Exception {
        Set<Class> result = command.compileTypes(container);

        assertEquals(0, result.size());
    }

    @Test
    public void compileTypes_should_return_types_of_one_container() throws Exception {
        container.get(NoConstructor.class);
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        Set<Class> expected = new HashSet<>();
        expected.add(NoConstructor.class);
        expected.add(NoMethodInterface.class);

        Set<Class> result = command.compileTypes(container);

        assertTrue(Objects.deepEquals(expected, result));
    }

    @Test
    public void compileTypes_should_return_types_of_container_tree() throws Exception {
        CommandContainer root = new CommandContainer();

        CommandContainer childA = new CommandContainer();
        childA.get(NoConstructor.class);

        CommandContainer childB = new CommandContainer();
        childB.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        container.addChild(childA);
        root.addChild(childB);
        root.addChild(container);

        Set<Class> expected = new HashSet<>();
        expected.add(NoConstructor.class);
        expected.add(NoMethodInterface.class);

        Set<Class> result = command.compileTypes(container);

        assertTrue(Objects.deepEquals(expected, result));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: FIND
    //----------------------------------------------------------------------------------------------

    @Test
    public void find_should_return_first_match_not_second() throws Exception {
        CommandContainer childA = new CommandContainer();
        childA.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        CommandContainer childB = new CommandContainer();
        childB.mapType(NoMethodAbstract.class, NoMethodImplementation.class);

        container.addChild(childA);
        container.addChild(childB);

        CommandContainer result = command.findOne(container, new Matcher() {
            @Override
            public boolean matches(CommandContainer container) {
                return container.mappings.containsValue(NoMethodImplementation.class);
            }
        });

        assertSame(childA, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: FIND ALL
    //----------------------------------------------------------------------------------------------

    @Test
    public void findAll_should_return_all_matches() throws Exception {
        CommandContainer parent = new CommandContainer();
        parent.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        CommandContainer child = new CommandContainer();
        child.mapType(NoMethodAbstract.class, NoMethodImplementation.class);

        container.setParent(parent);
        container.addChild(child);

        Set<CommandContainer> matches = command.findAll(container, new Matcher() {
            @Override
            public boolean matches(CommandContainer container) {
                return container.mappings.containsValue(NoMethodImplementation.class);
            }
        });

        assertTrue(matches.contains(parent));
        assertTrue(matches.contains(child));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: FIND MAPPING
    //----------------------------------------------------------------------------------------------

    @Test
    public void findMapping_should_return_instance() throws Exception {
        CommandContainer parentContainer = new CommandContainer();
        NoConstructor instance = parentContainer.get(NoConstructor.class);
        parentContainer.setParent(new CommandContainer());
        container.setParent(parentContainer);

        Object result = command.findMapping(container, NoConstructor.class);

        assertSame(instance, result);
    }

    @Test
    public void findMapping_should_return_mapping() throws Exception {
        CommandContainer parentContainer = new CommandContainer();
        parentContainer.mapType(NoMethodInterface.class, NoMethodImplementation.class);
        parentContainer.setParent(new CommandContainer());
        container.setParent(parentContainer);

        Object result = command.findMapping(container, NoMethodInterface.class);

        assertSame(NoMethodImplementation.class, result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: GET ROOT
    //----------------------------------------------------------------------------------------------

    @Test
    public void getRoot_should_return_self_if_no_parent() throws Exception {
        assertSame(container, command.getRoot(container));
    }

    @Test
    public void getRoot_should_return_root() throws Exception {
        CommandContainer root = new CommandContainer();

        CommandContainer parentA = new CommandContainer();
        parentA.setParent(root);

        container.setParent(parentA);

        CommandContainer childA = new CommandContainer();
        container.addChild(childA);

        CommandContainer childB = new CommandContainer();
        container.addChild(childB);

        assertSame(root, command.getRoot(childB));
    }

}
