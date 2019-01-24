package com.battlezone.megamachines.entities.CarComponents;

import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;
import com.battlezone.megamachines.entities.abstractCarComponents.Differential;

public class RearDifferential extends Differential {
    @Override
    public double getNewRPM() {
        double lowerSpeed = Math.min(leftWheel.getAngularVelocity(), rightWheel.getAngularVelocity());

        double newRPM = (60 * lowerSpeed / Math.PI) * finalDriveRatio;
        return newRPM;
    }

    /**
     * The wheel on the rear left of the car
     */
    private Wheel leftWheel;

    /**
     * The wheel on the rear right of the car
     */
    private Wheel rightWheel;

    private double finalDriveRatio = 3.2;

    public RearDifferential(Wheel leftWheel, Wheel rightWheel) {
        this.leftWheel = leftWheel;
        this.rightWheel = rightWheel;
    }

    @Override
    public void sendTorque(double torque) {
        torque *= finalDriveRatio;

        double inertiaPerWheel = (leftWheel.getWeight() * (leftWheel.getDiameter() / 2) * (leftWheel.getDiameter() / 2)) / 2;
        double wheelInertia = 2 * inertiaPerWheel;

        double angularAcceleration = torque / wheelInertia;

        leftWheel.applyAcceleration(angularAcceleration);
        rightWheel.applyAcceleration(angularAcceleration);
    }
}