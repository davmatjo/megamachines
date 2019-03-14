package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.components.abstracted.Wheel;

/**
 * A regular wheel
 */
public class RegularWheel extends Wheel {
    /**
     * The constructor
     * @param car The car which the wheel belongs to
     */
    public RegularWheel(RWDCar car) {
        this.car = car;
        this.angularVelocity = 0;
        weight = 70;
        diameter = 0.6;
        this.wheelPerformanceMultiplier = 3.0;
        this.wheelSidePerformanceMultiplier = 2.0;
        this.rollingResistance = 0.05;
    }
}
