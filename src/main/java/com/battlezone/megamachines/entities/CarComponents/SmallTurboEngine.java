package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.abstractCarComponents.Engine;
import com.battlezone.megamachines.entities.abstractCarComponents.Gearbox;

/**
 * This class simulates a small 1.6 litre turbocharged engine
 */
public class SmallTurboEngine extends Engine {
    /**
     * The constructor
     */
    public SmallTurboEngine(Gearbox gearbox) {
        this.gearbox = gearbox;
        this.weight = 150;
        this.setRPM(1500);
        this.minRPM = 1500;

        baseTorque = 300;
        delimitation = 4800;
    }
}
