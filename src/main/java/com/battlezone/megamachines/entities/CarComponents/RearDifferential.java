package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;
import com.battlezone.megamachines.entities.abstractCarComponents.Differential;

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
