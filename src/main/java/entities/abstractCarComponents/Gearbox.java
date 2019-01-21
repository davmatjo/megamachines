package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car gearbox
 */
public abstract class Gearbox extends EntityComponent {
    /**
     * The drive shaft connected to this gearbox;
     */
    protected DriveShaft driveShaft;

    /**
     * The losses of the gearbox as a number between 0 and 1, where 0 is perfectly efficient
     * and 1 means a total loss of power
     */
    private double gearboxLosses;

    /**
     * Gets the gearbox losses
     * @return The gearbox losses
     */
    public double getGearboxLosses() {
        return gearboxLosses;
    }

    /**
     * Sets the gearbox losses
     * @param gearboxLosses The percentage of power lost in the gearbox
     */
    protected void setGearboxLosses(double gearboxLosses) {
        this.gearboxLosses = gearboxLosses;
    }

    /**
     * Checks whether the gear needs to be changed
     * @param torque The engine's torque
     * @param Engine The car's engine
     */
    public abstract void checkShift(double torque, Engine sender);

    /**
     * Transforms torque and sends it to the DriveShaft
     * @param torque The engine's torque
     * @param Engine The car's engine
     */
    public abstract void sendTorque(double torque);

    /**
     * Gets the gearbox's new RPM
     */
    public abstract double getNewRPM();
}
