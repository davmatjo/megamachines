package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;

public class RegularWheel extends Wheel {
    public RegularWheel(RWDCar car) {
        this.car = car;
        this.angularVelocity = 0;
        weight = 70;
        diameter = 0.6;
        this.wheelPerformanceMultiplier = 3.0;
        this.wheelSidePerformanceMultiplier = 1.0;
        this.rollingResistance = 0.05;
    }
}
