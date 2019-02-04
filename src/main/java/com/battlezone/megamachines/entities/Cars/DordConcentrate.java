package com.battlezone.megamachines.entities.Cars;

import com.battlezone.megamachines.entities.CarComponents.*;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Vector3f;

/**
 * A regular car
 */
public class DordConcentrate extends RWDCar {

    /**
     * The constructor
     * @param x The x position this car starts in
     * @param y The y position this car starts in
     * @param scale The scale of this car
     * @param modelNumber The model number
     * @param colour The color
     */
    public DordConcentrate(double x, double y, float scale, int modelNumber, Vector3f colour) {
        super(x, y, scale, modelNumber, colour);
        flWheel = new RegularWheel(this);
        frWheel = new RegularWheel(this);
        blWheel = new RegularWheel(this);
        brWheel = new RegularWheel(this);

        backDifferential = new RearDifferential(blWheel, brWheel);

        driveShaft = new RWDDriveShaft(backDifferential);

        gearbox = new AutomaticSixSpeedGearbox(driveShaft, this);

        engine = new SmallTurboEngine(gearbox);

        carBody = new RegularChasis();

        this.wheelBase = 3.0;
        this.maximumSteeringAngle = 50.0;
        this.dragCoefficient = 0.4;
    }
}
