package entities.CarComponents;

import entities.abstractCarComponents.Engine;

/**
 * This class simulates a small 1.6 litre turbocharged engine
 */
public class SmallEngine extends Engine {

    @Override
    public double getTorqueAt(double RPM) {
        if (RPM >= 1500 && RPM <= 4500) {
            return 280;
        } else if (RPM > 4500 && RPM < 6000) {
            return 280 - ((RPM - 4500) / 10);
        } else {
            return 0;
        }
    }

    public SmallEngine() {
        this.weight = 150;
    }
}
