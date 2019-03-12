package com.battlezone.megamachines.entities;

import com.battlezone.megamachines.entities.abstractCarComponents.Wheel;

/**
 * An object which has wheels
 */
public interface WheeledObject {
    /**
     * Returns true if the wheel is one of the front wheels, false otherwise
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front wheel, false otherwise
     */
    public boolean isFrontWheel(Wheel wheel);

    /**
     * Returns true if the wheel is the front left hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front left wheel, false otherwise
     */
    public boolean isFrontLeftWheel(Wheel wheel);

    /**
     * Returns true if the wheel is the front right hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a front right wheel, false otherwise
     */
    public boolean isFrontRightWheel(Wheel wheel);

    /**
     * Returns true if the wheel is the back left hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a back left wheel, false otherwise
     */
    public boolean isBackLeftWheel(Wheel wheel);

    /**
     * Returns true if the wheel is the back right hweel
     *
     * @param wheel The wheel to be checked
     * @return True if the wheel is a back right wheel, false otherwise
     */
    public boolean isBackRightWheel(Wheel wheel);
}
