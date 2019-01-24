package com.battlezone.megamachines.entities.Cars;

import com.battlezone.megamachines.entities.CarComponents.*;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;

public class DordConcentrate extends RWDCar {

    public DordConcentrate(double x, double y, float scale, int modelNumber, Vector3f colour) {
        super(x, y, scale, modelNumber, colour);
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
