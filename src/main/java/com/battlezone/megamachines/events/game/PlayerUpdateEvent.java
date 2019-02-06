package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.entities.RWDCar;

import java.util.List;

public class PlayerUpdateEvent {

    private final byte[] data;
    private final int playerNumber;
    private boolean running;

    public PlayerUpdateEvent(byte[] data, int playerNumber, boolean running) {
        this.data = data;
        this.playerNumber = playerNumber;
        this.running = running;
    }

    public byte[] getData() {
        return data;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isRunning() {
        return running;
    }
}
