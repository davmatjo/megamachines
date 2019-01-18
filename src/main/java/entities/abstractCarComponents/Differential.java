package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car differential
 */
public abstract class Differential extends EntityComponent {
    /**
     * Sends torque to the wheels
     * @param torque The torque to be sent
     */
    public abstract void sendTorque(double torque);
}
