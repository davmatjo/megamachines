package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car engine
 */
public abstract class Engine extends EntityComponent {
    /**
     * The engine's current RPM
     */
    private double RPM;

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
    public abstract double getMaxTorque();
}
