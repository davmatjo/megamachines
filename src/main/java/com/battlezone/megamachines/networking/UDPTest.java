package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UDPTest {
    Client client;
    Server server;

    @Before
    public void setup(){
        server = new Server();
        server.start();
        client = new Client();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        int i = 0;
        while(i<10000) {
            String toSend = "hello server ";
            client.transmitMessage(toSend);
            System.out.println("Message " + i + " sent.");

            // TODO: see what messages you can get
//            String serverMessage = client.receiveMessage();
//            System.out.println(serverMessage + i);
//            assertEquals(toSend, serverMessage);

            i++;
        }
    }

    @After
    public void tearDown() {
        client.transmitMessage("end"); 
        client.close();
    }
}