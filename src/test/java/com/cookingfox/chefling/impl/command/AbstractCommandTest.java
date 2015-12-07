package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.impl.helper.Matcher;
import com.cookingfox.fixtures.chefling.NoConstructor;
import com.cookingfox.fixtures.chefling.NoMethodAbstract;
import com.cookingfox.fixtures.chefling.NoMethodImplementation;
import com.cookingfox.fixtures.chefling.NoMethodInterface;
import org.junit.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
public class AbstractCommandTest extends AbstractTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: COMPILE TYPES
    //----------------------------------------------------------------------------------------------

    @Test
    public void compileTypes_should_return_empty_if_no_instances_or_mappings() throws Exception {
        Set<Class> result = AbstractCommand.compileTypes(container);

        assertEquals(0, result.size());
    }

    @Test
    public void compileTypes_should_return_types_of_one_container() throws Exception {
        container.get(NoConstructor.class);
        container.mapType(NoMethodInterface.class, NoMethodImplementation.class);

        Set<Class> expected = new HashSet<>();
        expected.add(NoConstructor.class);
        expected.add(NoMethodInterface.class);

        Set<Class> result = AbstractCommand.compileTypes(container);

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

        Set<Class> result = AbstractCommand.compileTypes(container);

        assertTrue(Objects.deepEquals(expected, result));
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

        Set<CommandContainer> matches = AbstractCommand.findAll(container, new Matcher() {
            @Override
            public boolean matches(CommandContainer container) {
                return container.mappings.containsValue(NoMethodImplementation.class);
            }
        });

        assertTrue(matches.contains(parent));
        assertTrue(matches.contains(child));
    }

}
