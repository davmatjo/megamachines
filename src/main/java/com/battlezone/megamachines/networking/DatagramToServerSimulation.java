package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.fail;
import static org.lwjgl.glfw.GLFW.*;

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
    public void sendMessage_SuccessIfNoException() {
        // Create UDPPacketData object
        UDPPacketData packet = new UDPPacketData();

        // Define pool of keys and add WASD as GLFW variables as the input 
        ArrayList<Integer> pool = new ArrayList<>();
        pool.add(GLFW_KEY_W); pool.add(GLFW_KEY_A); pool.add(GLFW_KEY_S); pool.add(GLFW_KEY_D);

        // Loop messages
        for (int i = 0; i < 10000; i++) {
            // Generate random key presses
            int randomSize = 1 + new Random().nextInt(3);
            ArrayList<Integer> generatedList = new ArrayList<>();
            for ( int j = 0; j < randomSize; j++ )
                generatedList.add(pool.get(new Random().nextInt(4)));

            // Set new keyPresses
            packet.setKeyPresses(generatedList);

            // Create message to be sent from UDPPacket
            String message = packet.toString();

            // Send message
            try {
                client.sendMessage(message);
            }
            catch (Exception e) {
                fail("Should not throw exception when sending datagram.");
            }
        }
    }

    @After
    public void tearDown() {
        client.sendMessage("end");
        client.close();
    }
}