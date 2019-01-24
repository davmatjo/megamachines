package com.battlezone.megamachines.entities.CarComponents;

import java.util.ArrayList;
import java.util.List;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.abstractCarComponents.DriveShaft;
import com.battlezone.megamachines.entities.abstractCarComponents.Engine;
import com.battlezone.megamachines.entities.abstractCarComponents.Gearbox;

/**
 * This models an automatic six speed gearbox
 */
public class AutomaticSixSpeedGearbox extends Gearbox {
    RWDCar car;

    @Override
    public double getNewRPM() {
        return driveShaft.getNewRPM() * this.gearRatios.get(this.currentGear);
    }

    private ArrayList<Double> gearRatios =
            new ArrayList<Double>(List.of(0.0, 3.0, 2.1, 1.5, 1.0, 0.8, 0.6));

    /**
     * The constructor
     */
    public AutomaticSixSpeedGearbox(DriveShaft driveShaft, RWDCar car) {
        this.driveShaft = driveShaft;
        this.setGearboxLosses(0.1);
        currentGear = 1;
        this.lastShiftTime = System.currentTimeMillis();
        this.car = car;
    }

    @Override
    public void checkShift(double torque, Engine sender) {
        boolean canDownShift = true;
        boolean canUpShift = true;


        if (System.currentTimeMillis() - lastShiftTime < 100) {
            return;
        }

        if (currentGear == 1) {
            canDownShift = false;
        }

        if (currentGear == 6) {
            canUpShift = false;
        }

        if (this.getNewRPM() < this.car.getEngine().minRPM && canDownShift) {
            this.currentGear -= 1;
            this.car.getEngine().adjustRPM();
        } else if (this.getNewRPM() > this.car.getEngine().delimitation) {
            this.currentGear += 1;
            this.car.getEngine().adjustRPM();
        }
    }

    @Override
    public void sendTorque(double torque) {
        torque = (1 - this.getGearboxLosses()) * torque * gearRatios.get(currentGear);
        this.driveShaft.sendTorque(torque);
    }
}
