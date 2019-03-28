package com.battlezone.megamachines.messaging;

import com.battlezone.megamachines.events.Pooled;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MessageBusTest {

    private static boolean object1Sent;
    private static boolean object2Sent;

    @Test
    public void registerAndFireOnce() {
        MessageBus.register(this);
        MessageBus.register(new MessageBusTest());
        MessageBus.fire(new Object());
        assertTrue(object1Sent);
        assertTrue(object2Sent);
    }

    @EventListener
    public void testListener(Object o) {
        object1Sent = true;
    }

    @EventListener
    public void testListener2(Object o) {
        object2Sent = true;
        throw new RuntimeException();
    }

    @Test(expected = RuntimeException.class)
    public void doubleRegister() {
        MessageBus.register(this);
        MessageBus.register(this);
    }

    @Test
    public void fireAnIgnoredPooledEvent() {
        var pooled = mock(Pooled.class);
        MessageBus.fire(pooled);
        verify(pooled).delete();
    }
}