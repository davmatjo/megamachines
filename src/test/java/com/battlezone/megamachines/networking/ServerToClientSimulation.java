package com.battlezone.megamachines.networking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerToClientSimulation {
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

    }

    @Test
    public void createNewGameStatePacketFromString() {
        GameStatePacket newPacket = new GameStatePacket();
        assertEquals(newPacket.toString(), "c:0;t:0;p:;.");
        assertEquals(GameStatePacket.fromString("c:0;t:0;p:;").toString(), newPacket.toString());
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}
