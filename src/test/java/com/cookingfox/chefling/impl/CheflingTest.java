package com.cookingfox.chefling.impl;

import com.cookingfox.chefling.impl.command.CommandContainer;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link Chefling}.
 */
public class CheflingTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_be_private() throws Exception {
        Constructor<Chefling> constructor = Chefling.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: builder
    //----------------------------------------------------------------------------------------------

    @Test
    public void builder_should_return_new_builder() throws Exception {
        assertTrue(Chefling.builder() instanceof Chefling.Builder);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: createContainer
    //----------------------------------------------------------------------------------------------

    @Test
    public void createContainer_should_create_new_command_container() throws Exception {
        assertTrue(Chefling.createContainer() instanceof CommandContainer);
    }

}
