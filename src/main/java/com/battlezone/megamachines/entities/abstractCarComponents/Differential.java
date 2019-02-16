package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;

/**
 * The abstract representation of a car differential
 */
public abstract class Differential extends EntityComponent {
    /**
     * The wheel on the rear left of the car
     */
    protected Wheel leftWheel;

    /**
     * The wheel on the rear right of the car
     */
    protected Wheel rightWheel;

    /**
     * The final drive ratio of this differential
     */
    protected double finalDriveRatio;

    /**
     * Sends torque to the wheels
     * @param torque The torque to be sent
     */
    public void sendTorque(double torque, double l) {
        torque *= finalDriveRatio;

        double inertiaPerWheel = (leftWheel.getWeight() * (leftWheel.getDiameter() / 2) * (leftWheel.getDiameter() / 2)) / 2;
        double wheelInertia = 2 * inertiaPerWheel;

        double angularAcceleration = torque / wheelInertia;

        leftWheel.applyAcceleration(angularAcceleration, l);
        rightWheel.applyAcceleration(angularAcceleration, l);
    }

    /**
     * Gets the differential's new RPM (at the drive shaft end)
     */
    public double getNewRPM() {
        double lowerSpeed = Math.min(leftWheel.getAngularVelocity(), rightWheel.getAngularVelocity());

        double newRPM = (60 * lowerSpeed / Math.PI) * finalDriveRatio;
        return newRPM;
    }
}
