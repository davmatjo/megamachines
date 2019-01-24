package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of a car drive shaft
 */
public abstract class DriveShaft extends EntityComponent {
    /**
     * Sends torque to the rear differential
     * @param torque The torque to be sent
     */
    public abstract void sendTorque(double torque);

    /**
     * Gets the drive shaft's new RPM (which is the same to as the gearbox's)
     */
    public abstract double getNewRPM();
}
