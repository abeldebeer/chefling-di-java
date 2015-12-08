package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;
import com.cookingfox.chefling.impl.helper.Matcher;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Abel de Beer <abel@cookingfox.nl> on 04/12/15.
 */
class RemoveCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.RemoveCommand {

    private static final Set<Class> DO_NOT_REMOVE = new HashSet<>();

    static {
        DO_NOT_REMOVE.add(Container.class);
        DO_NOT_REMOVE.add(CommandContainer.class);
    }

    public RemoveCommand(CommandContainer container) {
        super(container);
    }

    @Override
    public void remove(final Class type) throws ContainerException {
        assertNonNull(type, "type");

        if (DO_NOT_REMOVE.contains(type)) {
            throw new RemoveTypeNotAllowedException(type);
        }

        CommandContainer hasType = findOne(_container, HasMappingMatcher.get(type));

        if (hasType == null) {
            return;
        }

        // call destroy method for life cycle objects
        lifeCycleDestroy(hasType.instances.get(type));

        synchronized (_container) {
            // remove type from maps
            hasType.instances.remove(type);
            hasType.mappings.remove(type);

            // find the containers that have a mapping TO this type
            final Set<CommandContainer> hasSubTypeContainers = findAll(_container, new Matcher() {
                @Override
                public boolean matches(CommandContainer container) {
                    return container.mappings.containsValue(type);
                }
            });

            if (hasSubTypeContainers.size() == 0) {
                return;
            }

            // Scenario: there are other mappings that are mapped to this type, e.g. when set using
            // `Container.mapType()`. Remove those mappings as well.

            LinkedList<Class> toRemoveTypes = new LinkedList<Class>();

            // find all mappings that refer to this mapping
            for (CommandContainer container : hasSubTypeContainers) {
                for (Map.Entry<Class, Object> mapping : container.mappings.entrySet()) {
                    if (mapping.getValue().equals(type)) {
                        toRemoveTypes.add(mapping.getKey());
                    }
                }
            }

            // remove the types
            for (Class toRemoveType : toRemoveTypes) {
                remove(toRemoveType);
            }
        }
    }
}
