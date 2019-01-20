package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.fail;

public class DatagramToServerSimulation {
    Client client;
    Server server;

    @Before
    public void setup() {
        server = new Server();
        server.start();
        client = new Client();
    }

    @Test
    public void whenCanSendAndReceivePacket_thenCorrect() {
        // Create UDPPacketData object
        UDPPacketData packet = new UDPPacketData();

        // Define pool of keys
        String pool = "wasd";

        // Loop messages
        for (int i = 0; i < 10000; i++) {
            // Generate random key presses
            int randomSize = 1 + new Random().nextInt(3);
            String generatedString = "" ;
            for ( int j = 0; j < randomSize; j++ )
                generatedString += pool.charAt(new Random().nextInt(4));

            // Set new keyPresses
            packet.setKeyPresses(generatedString);

            // Create message to be sent from UDPPacket
            String message = packet.toString();

            // Send message
            try {
                client.transmitMessage(message);
            }
            catch (Exception e) {
                fail("Should not throw exception when sending datagram.");
            }
        }
    }

    @After
    public void tearDown() {
        client.transmitMessage("end");
        client.close();
    }
}