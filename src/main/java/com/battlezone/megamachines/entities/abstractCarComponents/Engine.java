package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of a car engine
 */
public abstract class Engine extends EntityComponent {
    /**
     * The gearbox attached to this engine
     */
    protected Gearbox gearbox;

    /**
     * The engine's base torque
     */
    public double baseTorque;

    /**
     * The point where the engine starts to lose power
     */
    public double delimitation;

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
        this.RPM = Math.max(this.minRPM, gearbox.getNewRPM());
    }

    /**
     * Gets the maximum torque produced by the engine at an RPM value
     * @param RPM The RPM value
     * @return The maximum torque this engine produces at the specified RPM
     */
    public double getMaxTorque(double RPM) {
        if (RPM >= 1500 && RPM  <= delimitation) {
            return baseTorque;
        } else if (RPM  > delimitation && this.getRPM()  < 7000) {
            return baseTorque - ((this.getRPM()  - delimitation) / 10);
        } else {
            return 0;
        }
    }

    /**
     * Pushes the maximum torque to the gearbox.
     * In the future, we might add traction control here.
     * TODO: Add a traction control option
     */
    public void pushTorque(double accelerationAmount, double l){
        gearbox.checkShift(this);
        gearbox.sendTorque(this.getMaxTorque(this.getRPM()) * accelerationAmount, l);
    }
}
