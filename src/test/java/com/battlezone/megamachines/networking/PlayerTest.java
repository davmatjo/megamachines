package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.networking.server.player.Player;
import com.battlezone.megamachines.networking.server.player.PlayerConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.lwjgl.glfw.GLFW.glfwInit;

public class PlayerTest {
    private PlayerConnection connection;
    private Player player;
    private int modelNumber;
    private Vector3f vector3f;
    private RWDCar car;
    private Socket conn;
    private ObjectInputStream inputStream;
    private ObjectOutputStream objectOutputStream;

    @Before
    public void setUp() throws IOException {
        modelNumber = 1;
        vector3f = new Vector3f(1, 2, 3);
        conn = new Socket();
        inputStream = null;
        objectOutputStream = null;
        connection = new PlayerConnection(conn, inputStream, objectOutputStream);
        player = new Player(modelNumber, vector3f, connection, "");
        car = player.getCar();
    }

    @Test
    public void checkNewPlayer() {
        Player newPlayer = new Player(modelNumber, vector3f, connection, "");
        assertNotEquals(player, newPlayer);
    }

    @Test
    public void checkConnection() {
        assertEquals(player.getConnection(), connection);
    }

    @Test
    public void checkCar() {
        assertEquals(player.getCar(), car);
    }

    @Test
    public void checkPlayerConnection() {
        assertTrue(connection.getRunning());
        connection.close();
        connection.run();
        connection.setConnectionDroppedListener(null);
        connection.setLobbyAndStart(null);
        assertFalse(connection.getRunning());
        assertEquals(connection.getOutputStream(), objectOutputStream);
        assertNull(connection.getAddress());
    }

    @After
    public void end() {
        connection.close();
    }
}
