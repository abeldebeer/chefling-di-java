package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link CommandContainer}.
 */
public class CommandContainerTest {

    @Test
    public void constructor_should_map_instance_references() throws Exception {
        CommandContainer container = new CommandContainer();

        assertTrue(container.hasInstanceOrMapping(CheflingContainer.class));
        assertTrue(container.hasInstanceOrMapping(CommandContainer.class));
    }

}
