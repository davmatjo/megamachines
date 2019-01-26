package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.lwjgl.glfw.GLFW.*;

public class ClientToServerSimulation {
    Client client;
    Server server;
    int numberOfTimes;

    @Before
    public void setup() {
        server = new Server();
        server.start();
        client = new Client();
        client.start();

        // Set number of times a message will be sent
        numberOfTimes = 10000;
    }

    @Test
    public void sendMessage_SuccessIfNoException() {
        // Create ClientDataPacket object
        ClientDataPacket packet = new ClientDataPacket();

        // Define pool of keys and add WASD as GLFW variables as the input
        ArrayList<Integer> pool = new ArrayList<>();
        pool.add(GLFW_KEY_W); pool.add(GLFW_KEY_A); pool.add(GLFW_KEY_S); pool.add(GLFW_KEY_D);

        // Loop messages
        for (int i = 0; i < numberOfTimes; i++) {
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
    public void createNewDatagramPacketFromString_SuccessIfTheyCorrespond() {
        ClientDataPacket newPacket = new ClientDataPacket();
        assertEquals(newPacket.toString(), ClientDataPacket.emptyPacket());
        assertEquals(ClientDataPacket.fromString(ClientDataPacket.emptyPacket()).toString(), newPacket.toString());
    }

    @Test
    public void moreClientsSendMessagesToServer_SuccessIfNoException() {
        int numberOfClients = 8;
        try {
            ArrayList<Client> clients = new ArrayList<>();

            // Start Client threads
            for (int i = 0; i < numberOfClients; i++) {
                clients.add(new Client());
                clients.get(i).start();
            }

            // Send messages for each Client for a number of times
            for (int j = 0; j < numberOfTimes; j++) {
                for (int i = 0; i < numberOfClients; i++) {
                    clients.get(i).sendMessage(ClientDataPacket.fromString("t:" + i + ";k:;."));
                }
            }

            // Stop Client threads
            for (int i = 0; i < numberOfClients; i++) {
                clients.get(i).close();
            }
        } catch ( Exception e ) {
            fail("More clients (" + numberOfClients + ") failed to send messages to the server.");
        }
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}