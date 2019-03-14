package com.battlezone.megamachines.networking;

import org.junit.Assert;
import org.junit.Test;

public class ProtocolTest {

    @Test
    public void checkProtocolNumbers() {
        Protocol protocol = new Protocol();
        Assert.assertEquals(Protocol.DEFAULT_PORT, 7200);
        Assert.assertEquals(Protocol.JOIN_LOBBY, 0);
        Assert.assertNotEquals(protocol, null);
    }
}
