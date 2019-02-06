package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;
import com.battlezone.megamachines.entities.abstractCarComponents.Differential;

public class RearDifferential extends Differential {
    public RearDifferential(Wheel leftWheel, Wheel rightWheel) {
        this.leftWheel = leftWheel;
        this.rightWheel = rightWheel;

        this.finalDriveRatio = 3.2;
    }
}
