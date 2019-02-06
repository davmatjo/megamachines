package com.battlezone.megamachines.entities.CarComponents;

import java.util.ArrayList;
import java.util.List;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.DriveShaft;
import com.battlezone.megamachines.entities.abstractCarComponents.Engine;
import com.battlezone.megamachines.entities.abstractCarComponents.Gearbox;

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
        this.lastShiftTime = System.currentTimeMillis();
        this.car = car;

        this.gearRatios = new ArrayList<Double>(List.of(0.0, 3.0, 2.1, 1.5, 1.0, 0.8, 0.6));
    }
}
