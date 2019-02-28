package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;

/**
 * A race wheel. This is bigger, heavier and grippier than a regular wheel
 */
public class RaceWheel extends Wheel {
    /**
     * The constructor
     * @param car The car which the wheel belongs to
     */
    public RaceWheel(RWDCar car) {
        this.car = car;
        this.angularVelocity = 0;
        weight = 100;
        diameter = 1.0;
        this.wheelPerformanceMultiplier = 4.0;
        this.wheelSidePerformanceMultiplier = 3.0;
        this.rollingResistance = 0.1;
    }
}
