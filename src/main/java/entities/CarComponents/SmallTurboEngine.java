package entities.CarComponents;

import entities.abstractCarComponents.Engine;

/**
 * This class simulates a small 1.6 litre turbocharged engine
 */
public class SmallTurboEngine extends Engine {

    public double getMaxTorque(double RPM) {
        double baseTorque = 300;
        double delimitation = 4800;

        if (RPM >= 1500 && RPM  <= delimitation) {
            return baseTorque;
        } else if (RPM  > delimitation && this.getRPM()  < 7000) {
            return baseTorque - ((this.getRPM()  - 4500) / 10);
        } else {
            return 0;
        }
    }

    /**
     * The constructor
     */
    public SmallTurboEngine() {
        this.weight = 150;
        this.setRPM(1500);
        this.minRPM = 1500;
    }
}
