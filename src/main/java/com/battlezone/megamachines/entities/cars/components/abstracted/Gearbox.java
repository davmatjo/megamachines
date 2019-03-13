package com.battlezone.megamachines.entities.cars.components.abstracted;

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
     * The current gear
     */
    protected byte currentGear;

    /**
     * Gets the current gear
     * @return The current gear
     */
    public byte getCurrentGear() {
        return currentGear;
    }

    /**
     * Gets the current gear
     * @param currentGear The current gear
     */
    public void setCurrentGear(byte currentGear) {
        if (currentGear < 0) {
            currentGear = 0;
        } else if (currentGear > gearRatios.size() - 1) {
            currentGear = (byte)(gearRatios.size() - 1);
        }
        this.currentGear = currentGear;
    }

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

    /**
     * Engages or disengages the reverse gear
     * @param shouldReverse True if the reverse gear should be engaged, false otherwise
     */
    public void engageReverse(boolean shouldReverse) {
        if (shouldReverse) {
            this.currentGear = 0;
        } else {
            this.currentGear = 1;
        }
    }

    /**
     * Returns true if the gearbox is in reverse, false otherwise
     * @return True if the gearbox is in reverse, false otherwise
     */
    public boolean isOnReverse() {
        return (this.currentGear == 0);
    }

    /**
     * Checks whether the gear needs to be changed
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

        if (this.car.getEngine().getRPM() < this.car.getEngine().minRPM && canDownShift) {
            this.currentGear -= 1;
            this.car.getEngine().setRPM(this.getNewRPM());
        } else if (this.car.getEngine().getRPM() > this.car.getEngine().delimitation && canUpShift) {
            this.currentGear += 1;
            this.car.getEngine().setRPM(this.getNewRPM());
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
