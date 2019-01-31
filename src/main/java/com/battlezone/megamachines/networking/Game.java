package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.physics.PhysicsEngine;

import java.util.List;

public class Game implements Runnable {

    private final NewServer server;

    public Game(List<RWDCar> players, NewServer server) {
        for (var player : players) {
            PhysicsEngine.addCar(player);
        }
        this.server = server;
    }


    @Override
    public void run() {
        while (true) {
            PhysicsEngine.crank();
            server.sendPacket(new byte[6]);
        }
    }
}