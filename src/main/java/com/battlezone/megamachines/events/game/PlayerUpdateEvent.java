package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.entities.RWDCar;

import java.util.List;

public class PlayerUpdateEvent {

    private final List<RWDCar> cars;
    private final int playerNumber;
    private boolean running;

    public PlayerUpdateEvent(List<RWDCar> cars, int playerNumber, boolean running) {
        this.cars = cars;
        this.playerNumber = playerNumber;
        this.running = running;
    }

    public List<RWDCar> getCars() {
        return cars;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public boolean isRunning() {
        return running;
    }
}
