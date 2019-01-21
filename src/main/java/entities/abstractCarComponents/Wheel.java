package entities.abstractCarComponents;

import entities.EntityComponent;
import physics.WorldProperties;

/**
 * The abstract representation of a car wheel
 */
public abstract class Wheel extends EntityComponent {
    /**
     * A multiplier that makes the wheel more or less adherent to the road.
     */
    private double wheelPerformanceMultiplier = 1;

    /**
     * The angular velocity of the wheel
     */
    protected double angularVelocity = 0;

    /**
     * The diameter of the wheel
     */
    protected double diameter;

    /**
     * Applies acceleration to wheel
     */
    public abstract void applyAcceleration(double angularAcceleration);

    /**
     * Gets the maximum amount of friction between the wheel and the ground
     * @param slip
     * @return The amount of friction between the wheel and the road
     * //TODO: Update this for multiple types of road
     */
    protected double getFriction(double slip) {
        if (slip <= 6) {
            return wheelPerformanceMultiplier * WorldProperties.tyreFrictionRoadMultiplier * slip * (1.0 / 6.0);
        } else {
            /**
             * TODO: This is not quite right, but will do for now
             */
            return
                    Math.max(0.2, wheelPerformanceMultiplier *
                                WorldProperties.tyreFrictionRoadMultiplier *
                                (100 - 3 * (slip - 6)) / 100);
        }

    }

    /**
     * This method should be called once every pyhysics step
     * !!!ONLY BY THE CAR THIS WHEEL BELONGS TO!!!
     */
    public abstract void physicsStep();

    public double getDiameter() {
        return diameter;
    }
}
