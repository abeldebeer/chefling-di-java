package com.cookingfox.chefling.impl.command;

import com.cookingfox.fixtures.chefling.BasicContainerEvent;
import com.cookingfox.fixtures.chefling.BasicContainerEventListener;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * Created by abeldebeer on 16/03/16.
 */
public class DispatchCommandImplTest extends AbstractCommandTest {

    @Test
    public void dispatch_should_not_throw_for_no_listeners() throws Exception {
        container.dispatch(new BasicContainerEvent());
    }

    @Test
    public void dispatch_should_dispatch_event_to_listener() throws Exception {
        final BasicContainerEventListener listener = container.get(BasicContainerEventListener.class);
        final BasicContainerEvent event = new BasicContainerEvent();

        container.dispatch(event);

        assertSame(event, listener.receivedEvent);
    }

}
