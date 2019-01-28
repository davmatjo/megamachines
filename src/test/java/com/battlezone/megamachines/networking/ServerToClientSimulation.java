package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.PhysicalEntity;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.lwjgl.glfw.GLFW.*;

public class ServerToClientSimulation {
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

        // Start OpenGL so we can initialise RWDCar
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }
        long gameWindow = glfwCreateWindow(1, 1, "Test", 0, 0);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
    }

    @Test
    public void createNewGameStatePacket_successIfStringsCreatedCorrespond() {
        GameStatePacket newPacket = new GameStatePacket();
        assertEquals(newPacket.toString(), GameStatePacket.emptyPacket());
        assertEquals(GameStatePacket.fromString(GameStatePacket.emptyPacket()).toString(), newPacket.toString());

        RWDCar car = new DordConcentrate(1.0, 2.0, 1.25f, 1, new Vector3f(1f, 0.7f, 0.8f));
        car.setSpeed(3);
        car.setAngle(4);
        ArrayList<PhysicalEntity> tmp = new ArrayList<>();
        tmp.add(car); tmp.add(car);
        newPacket.setEntitiesData(tmp);

        assertEquals(GameStatePacket.fromString(newPacket.toString()).toString(), "c:0;t:0;p:0:x:1.0,y:2.0,a:4.0,s:3.0/1:x:1.0,y:2.0,a:4.0,s:3.0/;.");
    }

    @Test
    public void sendPacketToClient_successIfNoExceptionThrown() {
        // Start sending messages to the only Client connected
        for ( int i = 0; i < numberOfTimes; i++ ) {
            server.sendMessage(new GameStatePacket());
        }
    }

    @Test
    public void serverSendsMoreMessagesToClients_SuccesIfNoException() {
        int numberOfClients = 8;
        try {
            ArrayList<Client> clients = new ArrayList<>();

            // Start Client threads
            for (int i = 0; i < numberOfClients; i++) {
                clients.add(new Client());
                clients.get(i).start();
            }

            // Send messages to each Client for a number of times
            for (int j = 0; j < numberOfTimes; j++) {
                for (int i = 0; i < numberOfClients; i++) {
                    server.sendMessage(GameStatePacket.fromString(GameStatePacket.emptyPacket()));
                }
            }

            // Stop Client threads
            for (int i = 0; i < numberOfClients; i++) {
                clients.get(i).close();
            }
        } catch ( Exception e ) {
            fail("More clients (" + numberOfClients + ") failed to receive messages from the server.");
        }
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}
