package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.Container;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;
import com.cookingfox.chefling.impl.helper.Visitor;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @see com.cookingfox.chefling.api.command.RemoveCommand
 */
class RemoveCommand extends AbstractCommand implements com.cookingfox.chefling.api.command.RemoveCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    /**
     * Set of classes that should not be removed.
     */
    static final Set<Class> DO_NOT_REMOVE = new HashSet<>();

    /**
     * Static initializer.
     */
    static {
        DO_NOT_REMOVE.add(Container.class);
        DO_NOT_REMOVE.add(CommandContainer.class);
    }

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public RemoveCommand(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void remove(Class type) throws ContainerException {
        assertNonNull(type, "type");

        if (DO_NOT_REMOVE.contains(type)) {
            throw new RemoveTypeNotAllowedException(type);
        } else if (!_container.has(type)) {
            return;
        }

        removeMappingsForType(type);
        removeMappingsToType(type);
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Remove all instances and mappings for the type.
     *
     * @param type The type to remove.
     */
    protected void removeMappingsForType(Class type) {
        CommandContainer typeOwner = findOne(_container, HasMappingMatcher.get(type));

        // call destroy method for life cycle objects
        lifeCycleDestroy(typeOwner.instances.get(type));

        synchronized (_container) {
            // remove type from maps
            typeOwner.instances.remove(type);
            typeOwner.mappings.remove(type);
        }
    }

    /**
     * Remove all mappings TO this type. This can occur when {@link Container#mapType(Class, Class)}
     * was used.
     *
     * @param type The type to remove.
     * @throws ContainerException when the mapping cannot be removed.
     */
    protected void removeMappingsToType(final Class type) throws ContainerException {
        final Set<Class> toRemoveTypes = new LinkedHashSet<>();

        // find mappings TO this type in all container children
        visitAll(_container, new Visitor() {
            @Override
            public void visit(CommandContainer container) {
                for (Map.Entry<Class, Object> mapping : container.mappings.entrySet()) {
                    // mapping value is type: add to list of mappings to remove
                    if (mapping.getValue().equals(type)) {
                        toRemoveTypes.add(mapping.getKey());
                    }
                }
            }
        });

        // remove the mappings
        for (Class toRemoveType : toRemoveTypes) {
            remove(toRemoveType);
        }
    }

}
