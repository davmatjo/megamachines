package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.physics.WorldProperties;

public class RegularWheel extends Wheel {
    /**
     * The current slip ratio of the wheel
     */
    protected double slipRatio = 0.0;

    /**
     * The slip angle of the wheel
     */
    protected double slipAngle = 0.0;

    /**
     * The current friction coefficient between the wheel and the ground
     */
    protected double friction = 0.0;

    /**
     * The amount of longitudinal force the wheel puts into the ground (in the direction the car is pointing at)
     */
    protected double longitudinalForce = 0.0;

    /**
     * The amount of lateral force the wheel puts into the ground
     */
    protected double lateralForce = 0.0;

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
        computeSlipRatio();

        friction = this.getFriction(slipRatio);

        //Friction looks like a circle around the wheel
        //We want the vector sum of longitudinal and lateral friction to be
        //Shorter than the maximum vector currently permitted by the wheel
        double maximumFriction = this.getFriction(6);

        //The wheel is slipping too much
        //So the maximum vector shrinks
        if (slipRatio > 6) {
            maximumFriction = friction;
        }

        double maximumForce = maximumFriction * car.getLoadOnWheel() * WorldProperties.g;

        slipAngle = Math.atan((car.getLateralSpeed() + car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                                / car.getLongitudinalSpeed()) - Math.toRadians(car.getSteeringAngle(this)) * Math.signum(car.getLongitudinalSpeed());

        lateralForce = this.getLateralForce(slipAngle, car.getLoadOnWheel());

        longitudinalForce = friction * car.getLoadOnWheel() * WorldProperties.g;

        if (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2) > Math.pow(maximumForce, 2)) {
            double multiplyAmount = Math.pow(maximumForce, 2) / (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2));
            longitudinalForce *= multiplyAmount;
            lateralForce *= multiplyAmount;
        }

        double groundTorque = - (diameter / 2.0) * longitudinalForce;

        double angularAcceleration = groundTorque / (this.getWeight() * (this.diameter / 2.0) * (this.diameter / 2.0) / 2.0);

        this.angularVelocity += angularAcceleration * PhysicsEngine.getLengthOfTimestamp();
    }

    @Override
    public void physicsStep() {
        double carAngularAcceleration;
        if (car.isFrontWheel(this)) {
            carAngularAcceleration = Math.toRadians(Math.cos(car.getSteeringAngle(this))) * lateralForce * car.getDistanceToCenterOfWeightLongitudinally(this);
        } else {
            carAngularAcceleration = -lateralForce * Math.cos(Math.toRadians(car.getSteeringAngle(this))) * car.getDistanceToCenterOfWeightLongitudinally(this);
        }
        carAngularAcceleration *= PhysicsEngine.getLengthOfTimestamp();

        car.addForce(longitudinalForce, car.getAngle());
        car.addForce(lateralForce, car.getAngle() + 90);
        car.setAngularSpeed(car.getAngularSpeed() + carAngularAcceleration);
    }
}
