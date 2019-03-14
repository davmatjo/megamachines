package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.cars.components.abstracted.Wheel;
import com.battlezone.megamachines.entities.cars.components.abstracted.Differential;

/**
 * A rear wheel differential
 */
public class RearDifferential extends Differential {
    /**
     * The constructor
     * @param leftWheel The left wheel
     * @param rightWheel The right wheel
     */
    public RearDifferential(Wheel leftWheel, Wheel rightWheel) {
        this.leftWheel = leftWheel;
        this.rightWheel = rightWheel;

        this.finalDriveRatio = 3.2;
    }
}
