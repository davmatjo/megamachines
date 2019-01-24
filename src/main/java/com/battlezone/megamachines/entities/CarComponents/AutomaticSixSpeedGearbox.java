package com.battlezone.megamachines.entities.CarComponents;

import java.util.ArrayList;
import java.util.List;

import com.battlezone.megamachines.entities.abstractCarComponents.DriveShaft;
import com.battlezone.megamachines.entities.abstractCarComponents.Engine;
import com.battlezone.megamachines.entities.abstractCarComponents.Gearbox;

/**
 * This models an automatic six speed gearbox
 */
public class AutomaticSixSpeedGearbox extends Gearbox {
    @Override
    public double getNewRPM() {
        System.out.println(this.currentGear);
        return Math.max(1500, driveShaft.getNewRPM() * this.gearRatios.get(this.currentGear));
    }

    private ArrayList<Double> gearRatios =
            new ArrayList<Double>(List.of(0.0, 3.0, 2.1, 1.5, 1.0, 0.8, 0.6));

    /**
     * The constructor
     */
    public AutomaticSixSpeedGearbox(DriveShaft driveShaft) {
        this.driveShaft = driveShaft;
        this.setGearboxLosses(0.1);
        currentGear = 1;
        this.lastShiftTime = System.currentTimeMillis();
    }

    @Override
    public void checkShift(double torque, Engine sender) {
        if (System.currentTimeMillis() - lastShiftTime < 100) {
            return;
        }

        if (currentGear == 1) {
            double nextGearRPM = gearRatios.get(currentGear + 1) / gearRatios.get(currentGear) * sender.getRPM();

            if (sender.getRPM() > 1500 && sender.getMaxTorque(nextGearRPM) > torque) {
                currentGear += 1;
                sender.setRPM(nextGearRPM);
                this.lastShiftTime = System.currentTimeMillis();
            }
        } else if (currentGear < 6) {
            double prevGearRPM = gearRatios.get(currentGear - 1) / gearRatios.get(currentGear) * sender.getRPM();
            double nextGearRPM = gearRatios.get(currentGear + 1) / gearRatios.get(currentGear) * sender.getRPM();

            if (torque < sender.getMaxTorque(nextGearRPM)) {
                currentGear += 1;
                sender.setRPM(nextGearRPM);
                this.lastShiftTime = System.currentTimeMillis();
            } else if (torque < sender.getMaxTorque(prevGearRPM)) {
                currentGear -= 1;
                sender.setRPM(prevGearRPM);
                if (sender.getRPM() < 1500) {
                    sender.setRPM(1500);
                }
                this.lastShiftTime = System.currentTimeMillis();
            }
        } else if (currentGear == 6) {
            double prevGearRPM = gearRatios.get(currentGear - 1) / gearRatios.get(currentGear) * sender.getRPM();

            if (torque < sender.getMaxTorque(prevGearRPM)) {
                currentGear -= 1;
                sender.setRPM(prevGearRPM);
            }
            this.lastShiftTime = System.currentTimeMillis();
        }
    }

    @Override
    public void sendTorque(double torque) {
        torque = (1 - this.getGearboxLosses()) * torque * gearRatios.get(currentGear);
        this.driveShaft.sendTorque(torque);
    }
}
