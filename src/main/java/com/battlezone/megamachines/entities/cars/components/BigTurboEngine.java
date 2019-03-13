package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.cars.components.abstracted.Engine;
import com.battlezone.megamachines.entities.cars.components.abstracted.Gearbox;

/**
 * This class simulates a racing 4.0 litre turbocharged engine
 */
public class BigTurboEngine extends Engine {
    /**
     * The constructor
     */
    public BigTurboEngine(Gearbox gearbox) {
        this.gearbox = gearbox;
        this.weight = 400;
        this.setRPM(1500);
        this.minRPM = 1500;

        baseTorque = 550;
        delimitation = 7000;
    }
}