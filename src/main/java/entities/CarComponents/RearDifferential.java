package entities.CarComponents;

import entities.abstractCarComponents.Wheel;
import entities.abstractCarComponents.Differential;

public class RearDifferential extends Differential {
    @Override
    public void getNewRPM() {
        //TODO: DO THIS
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

        double inertiaPerWheel = (leftWheel.getWeight() * (leftWheel.diameter / 2) * (leftWheel.diameter / 2)) / 2;
        double wheelInertia = 2 * inertiaPerWheel;

        double angularAcceleration = torque / wheelInertia;

        leftWheel.applyAcceleration(angularAcceleration);
        rightWheel.applyAcceleration(angularAcceleration);
    }
}
