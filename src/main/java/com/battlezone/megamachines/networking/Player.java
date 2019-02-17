package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.Cars.DordConcentrate;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;

public class Player {

    private final RWDCar car;
    private final PlayerConnection connection;

    public Player(int modelNumber, Vector3f colour, PlayerConnection connection) {
        this.connection = connection;
        this.car = new DordConcentrate(0, 0, 1.25f, modelNumber, colour, 0, 1);
    }

    public RWDCar getCar() {
        return car;
    }

    public PlayerConnection getConnection() {
        return connection;
    }
}