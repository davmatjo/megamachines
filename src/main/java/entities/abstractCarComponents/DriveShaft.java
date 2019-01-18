package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car drive shaft
 */
public abstract class DriveShaft extends EntityComponent {
    /**
     * Sends torque to the rear differential
     * @param torque The torque to be sent
     */
    public abstract void sendTorque(double torque);
}
