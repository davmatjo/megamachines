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
        if (this.car.getSpeed() == 0) {
            if (this.angularVelocity == 0) {
                slipRatio = 0;
            } else if (this.angularVelocity > 0) {
                slipRatio = 6;
            } else {
                slipRatio = -6;
            }
        } else {
            slipRatio = (this.angularVelocity * (this.diameter / 2) - this.car.getSpeed()) / Math.abs(this.car.getSpeed());
        }

        System.out.println(PhysicsEngine.getLengthOfTimestamp());

        double friction = this.getFriction(slipRatio);
        System.out.println(friction);

        double force = friction * car.getLoadOnWheel() * WorldProperties.g;
        System.out.println(force);

        System.out.println(this.angularVelocity);
        this.angularVelocity -= force * (diameter / 2) * PhysicsEngine.getLengthOfTimestamp();
        System.out.println(this.angularVelocity);

        double carAcceleration = force * PhysicsEngine.getLengthOfTimestamp() / car.getWeight();
        System.out.println(carAcceleration);
        
        car.setSpeed(car.getSpeed() + carAcceleration);
    }
}
