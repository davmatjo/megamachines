package entities.CarComponents;

import entities.abstractCarComponents.Differential;
import entities.abstractCarComponents.DriveShaft;

/**
 * A drive shaft for a rear wheel drive car
 */
public class RWDDriveShaft extends DriveShaft {
    /**
     * The rear differential
     */
    private Differential backDifferential;

    /**
     * The inefficiency percentage
     */
    private double inefficiency = 0.1;

    @Override
    public void sendTorque(double torque) {
        torque = (1 - inefficiency) * torque;
        backDifferential.sendTorque(torque);
    }
}
