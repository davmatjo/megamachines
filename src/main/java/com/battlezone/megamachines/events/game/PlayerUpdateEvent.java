package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.world.track.Track;

import java.util.List;

public class PlayerUpdateEvent {

    private final Track track;
    private final List<RWDCar> cars;
    private final int playerNumber;
    private boolean running;

    public PlayerUpdateEvent(Track track, List<RWDCar> cars, int playerNumber, boolean running) {
        this.track = track;
        this.cars = cars;
        this.playerNumber = playerNumber;
        this.running = running;
    }

    public Track getTrack() {
        return track;
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
