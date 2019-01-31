package com.battlezone.megamachines.networking;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;

public class Player {

    private final RWDCar car;

    public Player(int modelNumber, Vector3f colour) {
        this.car = new RWDCar(0, 0, 1.25f, modelNumber, colour);
    }

    public RWDCar getCar() {
        return car;
    }
}