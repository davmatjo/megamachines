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
     * Gets the wheel performance modifier
     * @return The wheel performance modifier
     */
    public double getWheelPerformanceMultiplier() {
        return wheelPerformanceMultiplier;
    }

    /**
     * Sets the wheel performance modifier
     * @param wheelPerformanceMultiplier The wheel performance modifier to be set
     */
    public void setWheelPerformanceMultiplier(double wheelPerformanceMultiplier) {
        this.wheelPerformanceMultiplier = wheelPerformanceMultiplier;
    }

    /**
     * Gets the wheel side performance modifier
     * @return The wheel side performnce modifier
     */
    public double getWheelSidePerformanceMultiplier() {
        return wheelSidePerformanceMultiplier;
    }

    /**
     * Sets the wheel side performance modifier
     * @param wheelSidePerformanceMultiplier The wheel side performance modifier
     */
    public void setWheelSidePerformanceMultiplier(double wheelSidePerformanceMultiplier) {
        this.wheelSidePerformanceMultiplier = wheelSidePerformanceMultiplier;
    }

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
    protected double getFriction(double slip, WorldProperties worldProperties) {
        if (slip < 0.0) {
            return -getFriction(-slip, worldProperties);
        }

        if (Double.isNaN(slip)) {
            return 0;
        } else if (Double.isInfinite(slip)) {
            return 0.5 * wheelPerformanceMultiplier * Math.signum(slip);
        }

        if (slip <= 6.0) {
            return wheelPerformanceMultiplier * worldProperties.tyreFrictionMultiplier * slip * (1.0 / 6.0);
        } else {
            return
                    Math.max(0.5 * wheelPerformanceMultiplier, wheelPerformanceMultiplier *
                                worldProperties.tyreFrictionMultiplier *
                                (100.0 - 3.0 * (slip - 6)) / 100.0);
        }
    }

    //TODO: Once we introduce multiple terrain types, modify this
    /**
     * Returns the amount of lateral longitudinalForce generated by the wheel given a slip angle and the weight on the wheel
     * @param slipAngle The slip angle
     * @return The amount of lateral longitudinalForce
     */
    protected double getLateralForce(double slipAngle, double weightOnWheel, WorldProperties worldProperties) {
        double newtonsOnWheel = weightOnWheel * worldProperties.g;

        newtonsOnWheel *= wheelSidePerformanceMultiplier;

        if (Double.isNaN(slipAngle)) {
            return 0;
        }

        if (slipAngle < 0) {
            return -getLateralForce(-slipAngle, weightOnWheel, worldProperties);
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

        car.addForce(lateralForce, car.getAngle() + 90 + (car.getSteeringAngle(this) / 4), l);

        car.setAngularSpeed(car.getAngularSpeed() + carAngularAcceleration);
    }

    /**
     * Gets the force the wheel could exert given the friction, the load on the wheel and the gravitational constant
     * @param friction The friction
     * @param load The load on the wheel
     * @param g The gravitational constant
     * @return The force the wheel could exert
     */
    public static double getForce(double friction, double load, double g) {
        return friction * load * g;
    }

    /**
     * Returns this wheel's slip angle
     * @return This wheel's slip angle
     */
    public double getSlipAngle() {
        if (car.isFrontLeftWheel(this)) {
            return Math.toRadians(car.getSteeringAngle(this)) * Math.signum(car.getLongitudinalSpeed()) -
                    Math.atan((car.getLateralSpeed() + car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                            / Math.abs((car.getLongitudinalSpeed() - car.getWidth() * car.angularSpeed / 2)));
        } else if (car.isFrontRightWheel(this)) {
            return Math.toRadians(car.getSteeringAngle(this)) * Math.signum(car.getLongitudinalSpeed()) -
                    Math.atan((car.getLateralSpeed() + car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                            / Math.abs((car.getLongitudinalSpeed() + car.getWidth() * car.angularSpeed / 2)));
        } else if (car.isBackLeftWheel(this)) {
            return -Math.atan((car.getLateralSpeed() - car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                    / Math.abs((car.getLongitudinalSpeed() - car.getWidth() * car.angularSpeed / 2)));
        } else {
            return -Math.atan((car.getLateralSpeed() - car.angularSpeed * car.getDistanceToCenterOfWeightLongitudinally(this))
                    / Math.abs((car.getLongitudinalSpeed() + car.getWidth() * car.angularSpeed / 2)));
        }
    }

    /**
     * Normalizes this wheel's longitudinal and lateral forces
     * This is needed because the longitudinal and lateral forces are computed separately.
     * When their vector sum is longer than the maximum amount of force the wheel can transfer to the ground,
     * they are proportionally adjusted.
     * @param maximumForce The maximum force the wheel can exert to the ground
     */
    public void normalizeForces(double maximumForce) {
        if (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2) > Math.pow(maximumForce, 2)) {
            double multiplyAmount = Math.pow(maximumForce, 2) / (Math.pow(lateralForce, 2) + Math.pow(longitudinalForce, 2));
            longitudinalForce *= multiplyAmount;
            lateralForce *= multiplyAmount;
        }
    }

    /**
     * This method should be called once every pyhysics step
     * !!!ONLY BY THE CAR THIS WHEEL BELONGS TO!!!
     */
    public void computeNewValues(double l, WorldProperties worldProperties) {
        computeSlipRatio();

        friction = this.getFriction(slipRatio, worldProperties);

        //Friction looks like a circle around the wheel
        //We want the vector sum of longitudinal and lateral friction to be
        //Shorter than the maximum vector currently permitted by the wheel
        double maximumFriction = this.getFriction(6, worldProperties);

        //The wheel is slipping too much
        //So the maximum vector shrinks
        if (slipRatio > 6 || slipRatio < -6) {
            maximumFriction = Math.abs(friction);
        }

        double maximumForce = getForce(maximumFriction, car.getLoadOnWheel(this), worldProperties.g);

        lateralForce = this.getLateralForce(Math.toDegrees(getSlipAngle()), car.getLoadOnWheel(this), worldProperties);

        longitudinalForce = getForce(friction, car.getLoadOnWheel(this), worldProperties.g);

        //Cannot move horizontally when stopped, unless sliding
        //This is put in place to avoid floating point errors from moving the car when stopped
        if (car.getSpeed() < 0.1) {
            if (Math.abs(car.getLateralSpeed()) < 0.1) {
                car.setAngularSpeed(0);
                lateralForce = -car.getLateralSpeed();
                lateralForce /= l;
                lateralForce *= car.getWeight();
            }
        }

        normalizeForces(maximumForce);

        double groundTorque = - (diameter / 2.0) * longitudinalForce;

        angularAcceleration = - longitudinalForce / (this.getWeight() * (this.diameter / 2.0) / 2.0);

        this.angularVelocity += angularAcceleration * l;

        //Rolling resistance
        this.angularVelocity -= this.rollingResistance * (this.diameter / 2 * this.angularVelocity) * l;
    }

    /**
     * Gets this wheel's diameter
     * @return This wheel's diameter
     */
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
