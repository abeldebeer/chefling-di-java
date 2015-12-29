package com.cookingfox.chefling.impl.command;

import com.cookingfox.chefling.AbstractTest;
import com.cookingfox.chefling.api.exception.ContainerException;
import com.cookingfox.fixtures.chefling.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Unit tests for {@link DispatchCommandImpl}.
 */
public class DispatchCommandImplTest extends AbstractTest {

    @Test(expected = ContainerException.class)
    public void dispatch_should_throw_if_null_event() throws Exception {
        container.dispatch(null);
    }

    @Test
    public void dispatch_should_not_throw_if_no_instances() throws Exception {
        container.dispatch(new SimpleEvent());
    }

    @Test
    public void dispatch_should_not_throw_if_no_listeners() throws Exception {
        container.get(NoConstructor.class);

        container.dispatch(new SimpleEvent());
    }

    @Test
    public void dispatch_should_call_getListeners() throws Exception {
        ListenableWithCallLogReturnsNull listenable = container.get(ListenableWithCallLogReturnsNull.class);

        container.dispatch(new SimpleEvent());

        assertEquals(1, listenable.getListenersCalls.size());
    }

    @Test
    public void dispatch_should_not_throw_if_listenable_returns_null() throws Exception {
        container.get(ListenableWithCallLogReturnsNull.class);

        container.dispatch(new SimpleEvent());
    }

    @Test
    public void dispatch_should_not_throw_for_null_listeners() throws Exception {
        container.get(ListenableReturnsNullListeners.class);

        container.dispatch(new SimpleEvent());
    }

    @Test
    public void dispatch_should_skip_non_generic_listeners() throws Exception {
        ListenableReturnsNonGenericListener listenable = container.get(ListenableReturnsNonGenericListener.class);

        container.dispatch(new SimpleEvent());

        assertEquals(0, listenable.onEventCalls.size());
    }

    @Test
    public void dispatch_should_skip_listener_for_other_event() throws Exception {
        ListenableWithCallLogForSimpleEvent listenable = container.get(ListenableWithCallLogForSimpleEvent.class);

        Object event = new ArrayList<>();

        container.dispatch(event);

        assertEquals(0, listenable.onEventCalls.size());
    }

    @Test
    public void dispatch_should_pass_event_object() throws Exception {
        ListenableWithCallLogForSimpleEvent listenable = container.get(ListenableWithCallLogForSimpleEvent.class);

        Object event = new SimpleEvent();

        container.dispatch(event);

        assertEquals(1, listenable.onEventCalls.size());
        assertSame(event, listenable.onEventCalls.getFirst());
    }

}
