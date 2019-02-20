package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.physics.WorldProperties;

/**
 * The abstract representation of a car wheel
 */
public abstract class Wheel extends EntityComponent {
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
     * This wheel's angular acceleration
     */
    protected double angularAcceleration = 0;

    /**
     * The amount of lateral force the wheel puts into the ground
     */
    protected double lateralForce = 0.0;

    /**
     * The car this wheel belongs to
     */
    protected RWDCar car;

    /**
     * This wheel's rolling resistance
     */
    public double rollingResistance;

    /**
     * A multiplier that makes the wheel more or less adherent to the road.
     * //TODO:Adjust this for different handling
     */
    protected double wheelPerformanceMultiplier;

    /**
     * A multiplier that makes the wheel more or less good at turnng
     * //TODO:Adjust this for different handling
     */
    protected double wheelSidePerformanceMultiplier;

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

    public void setAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    /**
     * This function is used to compute the difference in angular velocity a wheel experiences when the car brakes
     * @param brakeAmount A number between 0 and 1 which expresses the amount of brake applied
     */
    public void brake(double brakeAmount, double l) {
        if (angularVelocity > 0) {
            this.angularVelocity -= brakeAmount * 180 * l;
            if (angularVelocity < 0) {angularVelocity = 0;}
        } else if (angularVelocity < 0) {
            this.angularVelocity += brakeAmount * 180 * l;
            if (angularVelocity > 0) {angularVelocity = 0;}
        }
    }

    /**
     * Applies acceleration to wheel
     */
    public void applyAcceleration(double angularAcceleration, double l) {
        this.angularVelocity += angularAcceleration * l;
    }

    /**
     * Gets the amount of friction between the wheel and the ground
     * @param slip
     * @return The amount of friction between the wheel and the road
     * //TODO: Update this for multiple types of road
     * //TODO: Change these values for more or less grip
     */
    protected double getFriction(double slip) {
        if (slip < 0.0) {
            return -getFriction(-slip);
        }

        if (Double.isNaN(slip)) {
            return 0;
        } else if (Double.isInfinite(slip)) {
            return 0.5 * wheelPerformanceMultiplier * Math.signum(slip);
        }

        if (slip <= 6.0) {
            return wheelPerformanceMultiplier * WorldProperties.tyreFrictionRoadMultiplier * slip * (1.0 / 6.0);
        } else {
            /**
             * TODO: This is not quite right, but will do for now
             */
            return
                    Math.max(0.5 * wheelPerformanceMultiplier, wheelPerformanceMultiplier *
                                WorldProperties.tyreFrictionRoadMultiplier *
                                (100.0 - 3.0 * (slip - 6)) / 100.0);
        }
    }

    //TODO: Once we introduce multiple terrain types, modify this
    /**
     * Returns the amount of lateral longitudinalForce generated by the wheel given a slip angle and the weight on the wheel
     * @param slipAngle The slip angle
     * @return The amount of lateral longitudinalForce
     */
    protected double getLateralForce(double slipAngle, double weightOnWheel) {
        double newtonsOnWheel = weightOnWheel * WorldProperties.g;

        newtonsOnWheel *= wheelSidePerformanceMultiplier;

        if (Double.isNaN(slipAngle)) {
            return 0;
        }

        if (slipAngle < 0) {
            return -getLateralForce(-slipAngle, weightOnWheel);
        }

        if (slipAngle < 4) {
            return newtonsOnWheel * 1.2 * slipAngle / 4.0;
        } else if (slipAngle < 36){
            return newtonsOnWheel * 1.2 - newtonsOnWheel * 0.2 * (slipAngle - 4.0) / 16.0;
        } else {
            return newtonsOnWheel * 0.8;
        }
    }

    /**
     * This method should be called once every pyhysics step
     * !!!ONLY BY THE CAR THIS WHEEL BELONGS TO!!!
     */
    public void physicsStep(double l) {
        double carAngularAcceleration;
        if (car.isFrontWheel(this)) {
            carAngularAcceleration = Math.cos(Math.toRadians(car.getSteeringAngle(this))) * lateralForce * car.getDistanceToCenterOfWeightLongitudinally(this);
        } else {
            carAngularAcceleration = -lateralForce * car.getDistanceToCenterOfWeightLongitudinally(this);
        }
        carAngularAcceleration *= l;
        //TODO: Tweak this
        carAngularAcceleration /= car.getRotationalInertia();

        car.addForce(longitudinalForce, car.getAngle(), l);

        //TODO: This is not quite right, find better alternative
        car.addForce(lateralForce, car.getAngle() + 90 + (car.getSteeringAngle(this) / 4), l);

        car.setAngularSpeed(car.getAngularSpeed() + carAngularAcceleration);
    }

    /**
     * This method should be called once every pyhysics step
     * !!!ONLY BY THE CAR THIS WHEEL BELONGS TO!!!
     */
    public void computeNewValues(double l) {
        computeSlipRatio();

        friction = this.getFriction(slipRatio);

        //Friction looks like a circle around the wheel
        //We want the vector sum of longitudinal and lateral friction to be
        //Shorter than the maximum vector currently permitted by the wheel
        double maximumFriction = this.getFriction(6);

        //The wheel is slipping too much
        //So the maximum vector shrinks
        if (slipRatio > 6 || slipRatio < -6) {
            maximumFriction = Math.abs(friction);
        }

        double maximumForce = maximumFriction * car.getLoadOnWheel(this) * WorldProperties.g;

        if (car.isFrontLeftWheel(this)) {
            slipAngle = Math.toRadians(car.getSteeringAngle(this)) * Math.signum(car.getLongitudinalSpeed()) -
                    Math.atan((car.getLateralSpeed() + car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                    / Math.abs((car.getLongitudinalSpeed() - car.getWidth() * car.angularSpeed / 2)));
        } else if (car.isFrontRightWheel(this)) {
            slipAngle = Math.toRadians(car.getSteeringAngle(this)) * Math.signum(car.getLongitudinalSpeed()) -
                    Math.atan((car.getLateralSpeed() + car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                            / Math.abs((car.getLongitudinalSpeed() + car.getWidth() * car.angularSpeed / 2)));
        } else if (car.isBackLeftWheel(this)) {
            slipAngle = -Math.atan((car.getLateralSpeed() - car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                    / Math.abs((car.getLongitudinalSpeed() - car.getWidth() * car.angularSpeed / 2)));
        } else {
            slipAngle = -Math.atan((car.getLateralSpeed() - car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                    / Math.abs((car.getLongitudinalSpeed() + car.getWidth() * car.angularSpeed / 2)));
        }

        lateralForce = Math.signum(slipAngle) * this.getLateralForce(Math.abs(Math.toDegrees(slipAngle)), car.getLoadOnWheel(this));

        longitudinalForce = friction * car.getLoadOnWheel(this) * WorldProperties.g;
//        if (car.getLongitudinalSpeed() != 0) {
//            longitudinalForce *= Math.signum(car.getLongitudinalSpeed());
//        }

        //Cannot move horizontally when stopped, unless sliding
        if (car.getSpeed() < 0.1) {
            if (Math.abs(car.getLateralSpeed()) < 0.1) {
                car.setAngularSpeed(0);
                lateralForce = -car.getLateralSpeed();
                lateralForce /= l;
                lateralForce *= car.getWeight();
            }
        }

        if (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2) > Math.pow(maximumForce, 2)) {
            double multiplyAmount = Math.pow(maximumForce, 2) / (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2));
            longitudinalForce *= multiplyAmount;
            lateralForce *= multiplyAmount;
        }

        double groundTorque = - (diameter / 2.0) * longitudinalForce;

        angularAcceleration = groundTorque / (this.getWeight() * (this.diameter / 2.0) * (this.diameter / 2.0) / 2.0);

        this.angularVelocity += angularAcceleration * l;

        //Rolling resistance
        this.angularVelocity -= this.rollingResistance * (this.diameter / 2 * this.angularVelocity) * l;
    }

    public double getDiameter() {
        return diameter;
    }

    /**
     * Computes the current slip ratio of the wheel
     */
    protected void computeSlipRatio() {
        slipRatio = ((this.angularVelocity * (this.diameter / 2.0)) / Math.abs(this.car.getLongitudinalSpeed()) - Math.signum(car.getLongitudinalSpeed())) * 100;
    }
}
