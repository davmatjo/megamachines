package entities.CarComponents;

import entities.abstractCarComponents.Engine;

/**
 * This class simulates a small 1.6 litre turbocharged engine
 */
public class SmallEngine extends Engine {

    @Override
    public double getTorqueAt(double RPM) {
        double baseTorque = 310;
        double delimitation = 5000;

        if (RPM >= 1500 && RPM <= delimitation) {
            return baseTorque;
        } else if (RPM > delimitation && RPM < 7000) {
            return baseTorque - ((RPM - 4500) / 10);
        } else {
            return 0;
        }
    }

    /**
     * The constructor
     */
    public SmallEngine() {
        this.weight = 150;
    }
}
