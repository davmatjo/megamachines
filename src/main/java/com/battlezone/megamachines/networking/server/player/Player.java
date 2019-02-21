package com.battlezone.megamachines.networking.server.player;

import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;

public class Player {

    private final RWDCar car;
    private final PlayerConnection connection;

    public Player(int modelNumber, Vector3f colour, PlayerConnection connection) {
        this.connection = connection;
        this.car = new AffordThoroughbred(0, 0, 1.25f, modelNumber, colour, 0, 1);
    }

    public RWDCar getCar() {
        return car;
    }

    public PlayerConnection getConnection() {
        return connection;
    }
}