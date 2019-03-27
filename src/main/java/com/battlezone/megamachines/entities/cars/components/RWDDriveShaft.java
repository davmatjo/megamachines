package com.battlezone.megamachines.entities.cars.components;

import com.battlezone.megamachines.entities.cars.components.abstracted.Differential;
import com.battlezone.megamachines.entities.cars.components.abstracted.DriveShaft;

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

    public RWDDriveShaft(Differential differential) {
        this.backDifferential = differential;
    }

    @Override
    public void sendTorque(double torque, double l) {
        torque = (1 - inefficiency) * torque;
        backDifferential.sendTorque(torque, l);
    }

    @Override
    public double getNewRPM() {
        return backDifferential.getNewRPM();
    }
}
