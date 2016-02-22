package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.TestCommand;
import com.cookingfox.chefling.impl.helper.Visitor;

/**
 * @see TestCommand
 */
class TestCommandImpl extends AbstractCommand implements TestCommand {

    public TestCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public synchronized void test() {
        visitAll(_container, new Visitor() {
            @Override
            public void visit(final CommandContainer container) {
                for (Class mapping : container.mappings.keySet()) {
                    container.get(mapping);
                }
            }
        });
    }

}
