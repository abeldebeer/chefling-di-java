package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.CheflingContainer;
import com.cookingfox.chefling.api.command.RemoveInstanceAndMappingCommand;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.api.exception.RemoveTypeNotAllowedException;
import com.cookingfox.chefling.impl.helper.CommandContainerVisitor;

import java.util.*;

/**
 * @see RemoveInstanceAndMappingCommand
 */
public class RemoveInstanceAndMappingCommandImpl extends AbstractCommand implements RemoveInstanceAndMappingCommand {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    /**
     * Set of classes that should not be removed.
     */
    protected static final Set<Class> DO_NOT_REMOVE;

    /**
     * Static initializer.
     */
    static {
        Set<Class> doNotRemove = new HashSet<>();
        doNotRemove.add(CheflingContainer.class);
        doNotRemove.add(CommandContainer.class);
        DO_NOT_REMOVE = Collections.unmodifiableSet(doNotRemove);
    }

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTORS
    //----------------------------------------------------------------------------------------------

    public RemoveInstanceAndMappingCommandImpl(CommandContainer container) {
        super(container);
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public void removeInstanceAndMapping(Class type) {
        assertNonNull(type, "type");

        if (DO_NOT_REMOVE.contains(type)) {
            throw new RemoveTypeNotAllowedException(type);
        } else if (!_container.hasInstanceOrMapping(type)) {
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
        CommandContainer typeOwner = findOneWithInstanceOrMapping(_container, type);

        // call destroy method for life cycle objects
        lifecycleDispose(typeOwner.instances.get(type));

        synchronized (_container) {
            // remove type from maps
            typeOwner.instances.remove(type);
            typeOwner.mappings.remove(type);
        }
    }

    /**
     * Remove all mappings TO this type. This can occur when {@link CheflingContainer#mapType(Class, Class)}
     * was used.
     *
     * @param type The type to remove.
     * @throws ContainerException when the mapping cannot be removed.
     */
    protected void removeMappingsToType(final Class type) {
        final Set<Class> toRemoveTypes = new LinkedHashSet<>();

        // find mappings TO this type in all container children
        visitAll(_container, new CommandContainerVisitor() {
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

        if (!toRemoveTypes.isEmpty()) {
            StringBuilder error = new StringBuilder("The mapping for `");
            error.append(type.getName());
            error.append("` can not be removed, because it has other types mapped to it: ");

            Iterator<Class> iterator = toRemoveTypes.iterator();

            while (iterator.hasNext()) {
                error.append('`');
                error.append(iterator.next().getName());
                error.append('`');

                if (iterator.hasNext()) {
                    error.append(", ");
                }
            }

            throw new RemoveTypeNotAllowedException(error.toString());
        }
    }

}
