package com.battlezone.megamachines.entities.abstractCarComponents;

import com.battlezone.megamachines.entities.EntityComponent;
import com.battlezone.megamachines.entities.RWDCar;

import java.util.ArrayList;

/**
 * The abstract representation of a car gearbox
 */
public abstract class Gearbox extends EntityComponent {
    /**
     * The gear ratios of this gearbox
     */
    protected ArrayList<Double> gearRatios;

    /**
     * The car this gearbox belongs to
     */
    protected RWDCar car;

    /**
     * The drive shaft connected to this gearbox;
     */
    protected DriveShaft driveShaft;

    /**
     * Time at which the last shift happened at
     */
    protected double lastShiftTime;

    /**
     * The current gear
     */
    public int currentGear;


    /**
     * The losses of the gearbox as a number between 0 and 1, where 0 is perfectly efficient
     * and 1 means a total loss of power
     */
    private double gearboxLosses;

    /**
     * Gets the gearbox losses
     * @return The gearbox losses
     */
    public double getGearboxLosses() {
        return gearboxLosses;
    }

    /**
     * Sets the gearbox losses
     * @param gearboxLosses The percentage of power lost in the gearbox
     */
    protected void setGearboxLosses(double gearboxLosses) {
        this.gearboxLosses = gearboxLosses;
    }

    public void engageReverse(boolean shouldReverse) {
        if (shouldReverse) {
            this.currentGear = 0;
        } else {
            this.currentGear = 1;
        }
    }

    public boolean isOnReverse() {
        return (this.currentGear == 0);
    }

    /**
     * Checks whether the gear needs to be changed
     * @param torque The engine's torque
     * @param sender The car's engine
     */
    public void checkShift(Engine sender) {
        boolean canDownShift = true;
        boolean canUpShift = true;

        if (this.currentGear == 0) {
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
            lastShiftTime = System.currentTimeMillis();
        } else if (this.getNewRPM() > this.car.getEngine().delimitation && canUpShift) {
            this.currentGear += 1;
            this.car.getEngine().adjustRPM();
            lastShiftTime = System.currentTimeMillis();
        }
    }

    /**
     * Transforms torque and sends it to the DriveShaft
     * @param torque The engine's torque
     */
    public void sendTorque(double torque, double l) {
        torque = (1 - this.getGearboxLosses()) * torque * gearRatios.get(currentGear);
        this.driveShaft.sendTorque(torque, l);
    }

    /**
     * Gets the gearbox's new RPM
     */
    public double getNewRPM() {
        return driveShaft.getNewRPM() * this.gearRatios.get(this.currentGear);
    }
}
