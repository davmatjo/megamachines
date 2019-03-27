package com.battlezone.megamachines.entities.cars;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.components.*;
import com.battlezone.megamachines.math.Vector3f;

/**
 * A regular car
 */
public class AffordThoroughbred extends RWDCar {
    /**
     * The constructor
     *
     * @param x           The x position this car starts in
     * @param y           The y position this car starts in
     * @param scale       The scale of this car
     * @param modelNumber The model number
     * @param colour      The color
     * @param lap
     * @param position
     */
    public AffordThoroughbred(double x, double y, float scale, int modelNumber, Vector3f colour, int lap, int position) {
        super(x, y, scale, modelNumber, colour, (byte) 0, (byte) 1, 3.0, 50.0, 1.0, 0.3, 2.0, 0.45);
        flWheel = new RegularWheel(this);
        frWheel = new RegularWheel(this);
        blWheel = new RegularWheel(this);
        brWheel = new RegularWheel(this);

        backDifferential = new RearDifferential(blWheel, brWheel);

        driveShaft = new RWDDriveShaft(backDifferential);

        gearbox = new AutomaticSixSpeedGearbox(driveShaft, this);

        engine = new SmallTurboEngine(gearbox);

        carBody = new RegularChasis();
    }
}
