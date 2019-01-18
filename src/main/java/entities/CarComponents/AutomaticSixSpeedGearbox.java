package entities.CarComponents;

import java.util.ArrayList;
import java.util.List;
import entities.abstractCarComponents.Engine;
import entities.abstractCarComponents.Gearbox;

/**
 * This models an automatic six speed gearbox
 */
public class AutomaticSixSpeedGearbox extends Gearbox {
    private ArrayList<Double> gearRatios =
            new ArrayList<Double>(List.of(0, 3, 2.1, 1.5, 1, 0.8, 0.6));
    private int currentGear;


    /**
     * The constructor
     */
    public AutomaticSixSpeedGearbox() {
        this.setGearboxLosses(0.1);
        currentGear = 1;
    }

    @Override
    public void checkShift(double torque, Engine sender) {
        if (currentGear == 1) {
            double nextGearRPM = gearRatios.get(currentGear + 1) / gearRatios.get(currentGear) * sender.getRPM();
            if (sender.getRPM() > 1500 && nextGearRPM * sender.getMaxTorque(nextGearRPM) > torque) {
                torque = sender.getMaxTorque(nextGearRPM);
                currentGear += 1;
                sender.setRPM(nextGearRPM);
            }
        } else if (currentGear < 6) {
            double prevGearRPM = gearRatios.get(currentGear - 1) / gearRatios.get(currentGear) * sender.getRPM();
            double nextGearRPM = gearRatios.get(currentGear + 1) / gearRatios.get(currentGear) * sender.getRPM();
            if (sender.getRPM() * torque < nextGearRPM * sender.getMaxTorque(nextGearRPM)) {
                torque = sender.getMaxTorque(nextGearRPM);
                currentGear += 1;
                sender.setRPM(nextGearRPM);
            } else if (sender.getRPM() * torque < prevGearRPM * sender.getMaxTorque(prevGearRPM)) {
                torque = sender.getMaxTorque(prevGearRPM);
                currentGear -= 1;
                sender.setRPM(prevGearRPM);
                if (sender.getRPM() < 1500) {
                    sender.setRPM(1500);
                }
            }
        } else if (currentGear == 6) {
            double prevGearRPM = gearRatios.get(currentGear - 1) / gearRatios.get(currentGear) * sender.getRPM();
            if (sender.getRPM() * torque < prevGearRPM * sender.getMaxTorque(prevGearRPM)) {
                torque = sender.getMaxTorque(prevGearRPM);
                currentGear -= 1;
                sender.setRPM(prevGearRPM);
            }
        }
    }

    @Override
    public void sendTorque(double torque) {
        torque = (1 - this.getGearboxLosses()) * torque;
    }
}
