package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.physics.WorldProperties;

/**
 * The abstract representation of a car wheel
 */
public abstract class Wheel extends EntityComponent {
    /**
     * A multiplier that makes the wheel more or less adherent to the road.
     * //TODO:Adjust this for different handling
     */
    private double wheelPerformanceMultiplier = 5.0;

    /**
     * The angular velocity of the wheel
     */
    protected double angularVelocity = 0.0;

    /**
     * The diameter of the wheel
     */
    protected double diameter;

    /**
     * Returns the wheel's angular velocity
     * @return The angular velocity
     */
    public double getAngularVelocity() {
        return this.angularVelocity;
    }

    /**
     * This function is used to compute the difference in angular velocity a wheel experiences when the car brakes
     * @param brakeAmount A number between 0 and 1 which expresses the amount of brake applied
     */
    public void brake(double brakeAmount) {
        if (angularVelocity > 0) {
            this.angularVelocity -= brakeAmount * 180 * PhysicsEngine.getLengthOfTimestamp();
            if (angularVelocity < 0) {angularVelocity = 0;}
        } else if (angularVelocity < 0) {
            this.angularVelocity += brakeAmount * 180 * PhysicsEngine.getLengthOfTimestamp();
            if (angularVelocity > 0) {angularVelocity = 0;}
        }
    }

    /**
     * Applies acceleration to wheel
     */
    public abstract void applyAcceleration(double angularAcceleration);

    /**
     * Gets the maximum amount of friction between the wheel and the ground
     * @param slip
     * @return The amount of friction between the wheel and the road
     * //TODO: Update this for multiple types of road
     * //TODO: Change these values for more or less grip
     */
    protected double getFriction(double slip) {
        if (slip < 0) {
            return -getFriction(-slip);
        }
        if (slip <= 6) {
            return wheelPerformanceMultiplier * WorldProperties.tyreFrictionRoadMultiplier * slip * (1.0 / 6.0);
        } else {
            /**
             * TODO: This is not quite right, but will do for now
             */
            return
                    Math.max(0.5, wheelPerformanceMultiplier *
                                WorldProperties.tyreFrictionRoadMultiplier *
                                (100.0 - 3.0 * (slip - 6)) / 100.0);
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
