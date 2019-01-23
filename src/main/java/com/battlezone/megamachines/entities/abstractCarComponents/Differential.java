package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of a car differential
 */
public abstract class Differential extends EntityComponent {
    /**
     * Sends torque to the wheels
     * @param torque The torque to be sent
     */
    public abstract void sendTorque(double torque);

    /**
     * Gets the differential's new RPM (at the drive shaft end)
     */
    public abstract void getNewRPM();
}
