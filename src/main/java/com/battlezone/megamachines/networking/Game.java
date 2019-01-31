package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.physics.PhysicsEngine;

import java.net.InetAddress;
import java.util.Map;

public class Game implements Runnable {

    private final NewServer server;
    private boolean running = true;

    public Game(Map<InetAddress, Player> players, NewServer server) {
        for (var player : players.values()) {
            PhysicsEngine.addCar(player.getCar());
        }
        this.server = server;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            PhysicsEngine.crank();
            server.sendPacket(new byte[6]);
        }
    }
}