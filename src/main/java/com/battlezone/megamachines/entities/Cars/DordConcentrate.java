package com.battlezone.megamachines.entities.Cars;

import com.battlezone.megamachines.entities.CarComponents.*;
import com.battlezone.megamachines.entities.RWDCar;

public class DordConcentrate extends RWDCar {

    public DordConcentrate(double x, double y, float scale, int modelNumber) {
        super(x, y, scale, modelNumber);
        flWheel = new RegularWheel(this);
        frWheel = new RegularWheel(this);
        blWheel = new RegularWheel(this);
        brWheel = new RegularWheel(this);

        backDifferential = new RearDifferential(blWheel, brWheel);

        driveShaft = new RWDDriveShaft(backDifferential);

        gearbox = new AutomaticSixSpeedGearbox(driveShaft);

        engine = new SmallTurboEngine(gearbox);

        carBody = new RegularChasis();
    }
}
