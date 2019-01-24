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
    public void createNewGameStatePacket_successIfStringsCreatedCorrespond() {
        GameStatePacket newPacket = new GameStatePacket();
        assertEquals(newPacket.toString(), "c:0;t:0;p:;.");
        assertEquals(GameStatePacket.fromString("c:0;t:0;p:;").toString(), newPacket.toString());

//        ArrayList<PhysicalEntity> tmp = new ArrayList<>();
//        Vector3f a = new Vector3f(0, 0, 0);
//        RWDCar car = new RWDCar(0, 0, 0, 0, a);
//        tmp.add(car);
//        newPacket.setEntitiesData(tmp);
//        System.out.println(newPacket.toString()); 
    }

    @After
    public void tearDown() {
        server.close();
        client.close();
    }
}
