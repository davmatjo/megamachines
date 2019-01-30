package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.physics.WorldProperties;

public class RegularWheel extends Wheel {
    /**
     * The current slip ratio of the wheel
     */
    double slipRatio;

    /**
     * The current friction coefficient between the wheel and the ground
     */
    double friction;

    /**
     * The amount of force the wheel puts into the ground (in the direction the car is pointing at)
     */
    double force;

    /**
     * The car this wheel belongs to
     */
    private RWDCar car;

    public RegularWheel(RWDCar car) {
        this.car = car;
        this.angularVelocity = 0;
        weight = 70;
        diameter = 0.6;
    }


    @Override
    public void applyAcceleration(double angularAcceleration) {
        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();
    }

    /**
     * Computes the current slip ratio of the wheel
     */
    protected void computeSlipRatio() {
        if (this.car.getSpeed() == 0) {
            if (this.angularVelocity == 0) {
                slipRatio = 0;
            } else if (this.angularVelocity > 0) {
                slipRatio = 6.0;
            } else {
                slipRatio = -6.0;
            }
        } else {
            slipRatio = (this.angularVelocity * (this.diameter / 2.0) - this.car.getSpeed()) / Math.abs(this.car.getSpeed()) * 100.0;
        }
    }

    @Override
    public void computeNewValues() {
        //TODO: A car that's breaking has a negative slip ratio. We could try -1 for each wheel and see where it goes
        computeSlipRatio();

        friction = this.getFriction(slipRatio);
        force = friction * car.getLoadOnWheel() * WorldProperties.g;

        double groundTorque = - (diameter / 2.0) * force;

        double angularAcceleration = groundTorque / (this.getWeight() * (this.diameter / 2.0) * (this.diameter / 2.0) / 2.0);

        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();
    }

    @Override
    public void physicsStep() {
        double carAcceleration = force * PhysicsEngine.getLengthOfTimestamp() / car.getWeight();

        car.setSpeed(car.getSpeed() + carAcceleration);

//        System.out.println(car.getSpeed() + "   GEAR " + car.getGearbox().currentGear
//                + " RPM " + car.getGearbox().getNewRPM()+ "  SLIP " + slipRatio);
    }
}
