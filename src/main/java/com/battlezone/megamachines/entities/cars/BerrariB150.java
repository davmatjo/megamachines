package com.battlezone.megamachines.entities.cars;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.components.*;
import com.battlezone.megamachines.math.Vector3f;

/**
 * A race car
 */
public class BerrariB150 extends RWDCar {
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
    public BerrariB150(double x, double y, float scale, int modelNumber, Vector3f colour, int lap, int position, String name) {
        super(x, y, scale, modelNumber, colour, (byte) 0, (byte) 1, 3.0, 50.0, 1.5, 0.15, 3.0, 0.3, name);
        flWheel = new RaceWheel(this);
        frWheel = new RaceWheel(this);
        blWheel = new RaceWheel(this);
        brWheel = new RaceWheel(this);

        backDifferential = new RearDifferential(blWheel, brWheel);

        driveShaft = new RWDDriveShaft(backDifferential);

        gearbox = new AutomaticSixSpeedGearbox(driveShaft, this);

        engine = new BigTurboEngine(gearbox);

        carBody = new RaceChasis();
    }
}
