package com.battlezone.megamachines.networking;

import org.junit.*;
import static org.junit.Assert.*;

public class UDPTest {
    EchoClient client;

    @Before
    public void setup(){
        new EchoServer().start();
        client = new EchoClient();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        int i = 0;
        while(i<10000) {
            String echo = client.sendEcho("hello server" + i);
            assertEquals
                    ("hello server" + i, echo);
            System.out.println("client:" + echo);
            i++;
        }
        String echo = client.sendEcho("server is working");
        assertFalse(echo.equals("hello server"));
    }

    @After
    public void tearDown() {
        client.sendEcho("end");
        client.close();
    }
}