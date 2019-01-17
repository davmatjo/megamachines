package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car engine
 */
public abstract class Engine extends EntityComponent {
    /**
     * Gets the maximum torque produced by the engine at an RPM value
     * @param RPM The RPM value
     */
    public abstract double getTorqueAt(double RPM);
}
