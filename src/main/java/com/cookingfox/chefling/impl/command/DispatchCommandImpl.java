package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.api.command.DispatchCommand;
import com.cookingfox.chefling.api.event.ContainerEvent;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.chefling.impl.helper.RegisteredListener;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by abeldebeer on 16/03/16.
 */
class DispatchCommandImpl extends AbstractCommand implements DispatchCommand {

    public DispatchCommandImpl(CommandContainer container) {
        super(container);
    }

    @Override
    public void dispatch(final ContainerEvent event) {
        // FIXME: 16/03/16 Check not null

        final Class eventClass = event.getClass();
        final Set<RegisteredListener> listeners = _container.listeners.get(eventClass);

        if (listeners == null) {
            // FIXME: 16/03/16 Handle no listeners for event
            throw new ContainerException("NO LISTENERS!");
        }

        final ExecutorService eventDispatcher = Executors.newCachedThreadPool();

        for (final RegisteredListener listener : listeners) {
            final Object subscriber = listener.subscriber;

            eventDispatcher.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    listener.method.invoke(subscriber, event);

                    return null;
                }
            });
        }

        eventDispatcher.shutdown();
    }

}
