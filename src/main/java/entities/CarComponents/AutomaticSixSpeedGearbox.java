package entities.CarComponents;

import java.util.ArrayList;
import java.util.List;
import entities.abstractCarComponents.Engine;
import entities.abstractCarComponents.Gearbox;

import java.util.Arrays;

/**
 * This models an automatic six speed gearbox
 */
public class AutomaticSixSpeedGearbox extends Gearbox {
    private ArrayList<Double> gearRatios =
            new ArrayList<Double>(List.of(3, 2.1, 1.5, 1, 0.8, 0.6));
    private int currentGear;


    /**
     * The constructor
     */
    public AutomaticSixSpeedGearbox() {
        this.setGearboxLosses(0.1);
        currentGear = 1;
    }

    @Override
    public void sendTorque(double torque, Engine sender) {
        //We will first determine if the gear needs to be changed
        if (currentGear < 6) {
            double nextGearRPM = gearRatios.get(currentGear + 1) / gearRatios.get(currentGear);
            if (sender.getRPM() * torque < nextGearRPM * sender.getMaxTorque(nextGearRPM)) {
                torque = sender.getMaxTorque(nextGearRPM);
                currentGear += 1;
                sender.setRPM(nextGearRPM);
            }
        } else if (currentGear > 2) {
            double prevGearRPM = gearRatios.get(currentGear - 1) / gearRatios.get(currentGear);
            if (sender.getRPM() * torque < prevGearRPM * sender.getMaxTorque(prevGearRPM)) {
                torque = sender.getMaxTorque(prevGearRPM);
                currentGear -= 1;
                sender.setRPM(prevGearRPM);
            }
        } else if (currentGear == 2) {
            if (sender.getRPM() * torque < sender.minRPM * sender.getMaxTorque(sender.minRPM)) {
                torque = sender.getMaxTorque(sender.minRPM);
                currentGear -= 1;
                sender.setRPM(sender.minRPM);
            }
        }
        
        torque = (1 - this.getGearboxLosses()) * torque;
    }
}
