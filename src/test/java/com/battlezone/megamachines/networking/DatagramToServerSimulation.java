package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
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
        // Create ClientDataPacket object
        ClientDataPacket packet = new ClientDataPacket();

        // Define pool of keys and add WASD as GLFW variables as the input
        ArrayList<Integer> pool = new ArrayList<>();
        pool.add(GLFW_KEY_W); pool.add(GLFW_KEY_A); pool.add(GLFW_KEY_S); pool.add(GLFW_KEY_D);

        // Loop messages
        for (int i = 0; i < 10000; i++) {
            // Generate a random key press
            Integer randomKey = pool.get(new Random().nextInt(4));

            // Define boolean to see whether to remove or not key
            boolean addKey = new Random().nextBoolean();

            // Set new keyPresses
            if ( addKey == true )
                packet.addKeyPress(randomKey);
            else
                packet.removeKeyPress(randomKey);

            // Send message
            try {
                client.sendMessage(packet);
            }
            catch (Exception e) {
                fail("Should not throw exception when sending datagram.");
            }
        }
    }

    @Test
    public void createNewDatagramPacketFromString() {
        ClientDataPacket newPacket = new ClientDataPacket();
        assertEquals(newPacket.toString(), "t:0;k:;.");
        assertEquals(ClientDataPacket.fromString("k:;t:0;.").toString(), newPacket.toString());
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}