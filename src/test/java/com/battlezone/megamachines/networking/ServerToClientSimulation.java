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
import static org.lwjgl.glfw.GLFW.*;

public class ServerToClientSimulation {
    Client client;
    Server server;

    @Before
    public void setup() {
        server = new Server();
        server.start();
        client = new Client();
        client.start();

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
        assertEquals(newPacket.toString(), "c:0;t:0;p:;.");
        assertEquals(GameStatePacket.fromString("c:0;t:0;p:;").toString(), newPacket.toString());

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
        GameStatePacket packet = new GameStatePacket();
        // Initialise by sending Client packet to Server
        client.sendMessage(new ClientDataPacket());
        for ( int i = 0; i < 100; i++ ) {
            server.sendMessage(packet);
        }
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}
