package entities.abstractCarComponents;

import entities.EntityComponent;

/**
 * The abstract representation of a car wheel
 */
public abstract class Wheel extends EntityComponent {
    /**
     * The angular velocity of the wheel
     */
    protected double angularVelocity;

    /**
     * The diameter of the wheel
     */
    public double diameter;

    /**
     * Applies acceleration to wheel
     */
    public abstract void applyAcceleration(double angularAcceleration);
}
