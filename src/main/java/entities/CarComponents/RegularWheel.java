package entities.CarComponents;

import entities.RWDCar;
import entities.abstractCarComponents.Wheel;
import physics.PhysicsEngine;
import physics.WorldProperties;

public class RegularWheel extends Wheel {
    /**
     * The diameter of the wheel in meters
     */
    private double diameter = 0.6;

    /**
     * The weight in kg of the wheel
     */
    private double weight = 70;

    /**
     * The car this wheel belongs to
     */
    private RWDCar car;


    @Override
    public void applyAcceleration(double angularAcceleration) {
        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();

        //TODO: A car that's breaking has a negative slip ratio. We could try -1 for each wheel and see where it goes
        double slipRatio = (this.angularVelocity * (this.diameter / 2) - this.car.getSpeed()) / Math.abs(this.car.getSpeed());

        double friction = this.getFriction(slipRatio);

        double maximumForce = friction * car.getLoadOnWheel() * WorldProperties.g;
    }


}
