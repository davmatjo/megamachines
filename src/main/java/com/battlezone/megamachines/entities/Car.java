package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.entities.cars.components.abstracted.*;

public interface Car {
    /**
     * Returns the car's body
     * @return The car's body
     */
    public CarBody getCarBody();

    /**
     * Returns this car's back differential
     * @return This car's back differential
     */
    public Differential getBackDifferential();

    /**
     * Returns this car's drive shaft
     * @return This car's drive shaft
     */
    public DriveShaft getDriveShaft();

    /**
     * Returns the engine of the car
     *
     * @return The engine of the car
     */
    public Engine getEngine();

    /**
     * Returns the gearbox of the car
     *
     * @return The gearbox of the car
     */
    public Gearbox getGearbox();
}
