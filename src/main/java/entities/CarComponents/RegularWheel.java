package entities.CarComponents;

import entities.abstractCarComponents.Wheel;
import physics.PhysicsEngine;

public class RegularWheel extends Wheel {
    /**
     * The diameter of the wheel in meters
     */
    private double diameter = 0.6;

    /**
     * The weight in kg of the wheel
     */
    private double weight = 70;


    @Override
    public void applyAcceleration(double angularAcceleration) {
        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();
    }
}
