package entities.CarComponents;

import entities.RWDCar;
import entities.abstractCarComponents.Wheel;
import physics.PhysicsEngine;
import physics.WorldProperties;

public class RegularWheel extends Wheel {

    /**
     * The car this wheel belongs to
     */
    private RWDCar car;

    public RegularWheel(RWDCar car) {
        this.car = car;
        weight = 70;
        diameter = 0.6;
    }


    @Override
    public void applyAcceleration(double angularAcceleration) {
        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();
    }

    @Override
    public void physicsStep() {
        //TODO: A car that's breaking has a negative slip ratio. We could try -1 for each wheel and see where it goes
        double slipRatio;
        if (this.car.getSpeed() != 0) {
            slipRatio = (this.angularVelocity * (this.diameter / 2) - this.car.getSpeed()) / Math.abs(this.car.getSpeed());
        } else {
            slipRatio = 6;
        }

        double friction = this.getFriction(slipRatio);

        double force = friction * car.getLoadOnWheel() * WorldProperties.g;
        this.angularVelocity -= force * (diameter / 2) * PhysicsEngine.getLengthOfTimestamp();

        double carAcceleration = force / car.getWeight();

        double deltaV = PhysicsEngine.getLengthOfTimestamp() * carAcceleration;

        car.setSpeed(car.getSpeed() + deltaV);
    }
}
