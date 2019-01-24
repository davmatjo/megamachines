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

        // Start OpenGL so we can initialise RWDCar
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }
        // Create window
        long gameWindow = glfwCreateWindow(1, 1, "Test", 0, 0);
        // Initialise openGL states
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
        tmp.add(car);
        tmp.add(car);
        newPacket.setEntitiesData(tmp);
        System.out.println(GameStatePacket.fromString(newPacket.toString()).toString());
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}
