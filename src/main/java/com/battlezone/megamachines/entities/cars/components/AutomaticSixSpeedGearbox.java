package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.components.abstracted.DriveShaft;
import com.battlezone.megamachines.entities.cars.components.abstracted.Gearbox;

import java.util.ArrayList;
import java.util.List;

/**
 * This models an automatic six speed gearbox
 */
public class AutomaticSixSpeedGearbox extends Gearbox {
    /**
     * The constructor
     */
    public AutomaticSixSpeedGearbox(DriveShaft driveShaft, RWDCar car) {
        this.driveShaft = driveShaft;
        this.setGearboxLosses(0.1);
        currentGear = 1;
        this.car = car;

        this.gearRatios = new ArrayList<Double>(List.of(-2.5, 3.0, 2.1, 1.5, 1.0, 0.8, 0.6));
    }
}
