package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car engine
 */
public abstract class Engine extends EntityComponent {
    /**
     * The gearbox attached to this engine
     */

    Gearbox gearbox;
    /**
     * The engine's current RPM
     */
    private double RPM;

    /**
     * The engine's minimum RPM
     */
    public double minRPM;

    /**
     * Gets the engine's RPM
     * @return The engine's RPM
     */
    public double getRPM() {
        return RPM;
    }

    /**
     * Sets the engine's RPM
     * @param RPM The engine's RPM
     */
    public void setRPM(double RPM) {
        this.RPM = RPM;
    }
    /**
     * Gets the maximum torque produced by the engine at an RPM value
     * @param RPM The RPM value
     */
    public abstract double getMaxTorque(double RPM);

    /**
     * Pushes the maximum torque to the gearbox.
     * In the future, we might add traction control here.
     * TODO: Add a traction control option
     */
    public void pushTorque(){
        gearbox.checkShift(this.getMaxTorque(this.getRPM()), this);
        gearbox.sendTorque(this.getMaxTorque(this.getRPM()));
    }

    /**
     * Gets the engine's new RPM
     */
    public abstract void getNewRPM();
}
