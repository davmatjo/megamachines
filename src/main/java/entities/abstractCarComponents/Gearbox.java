package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car gearbox
 */
public abstract class Gearbox extends EntityComponent {
    /**
     * The drive shaft connected to this gearbox;
     */
    private DriveShaft driveShaft;

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
     * Transforms torque and sends it to the DriveShaft
     * @param torque
     */
    public abstract void sendTorque(double torque, Engine sender);
}
