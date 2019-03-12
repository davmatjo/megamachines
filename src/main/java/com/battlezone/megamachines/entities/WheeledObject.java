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

    /**
     * Gets the front left wheel of the car
     * @return The front left wheel of the car
     */
    public Wheel getFlWheel();

    /**
     * Gets the front right wheel of the car
     * @return The front right wheel of the car
     */
    public Wheel getFrWheel();

    /**
     * Gets the back left wheel of the car
     * @return The back left wheel of the car
     */
    public Wheel getBlWheel();

    /**
     * Gets the back right wheel of the car
     * @return The back right wheel of the car
     */
    public Wheel getBrWheel();

    /**
     * Gets this wheeled object's weight
     * @return this wheeled object's weight
     */
    public double getWeight();

    /**
     * Gets the distance from the center of weight to the rear axle
     *
     * @return The distance from the center of weight to the rear axle
     */
    public double getDistanceCenterOfWeightRearAxle();

    /**
     * Gets the distance from the center of weight to the front axle
     *
     * @return The distance from the center of weight to the front axle
     */
    public double getDistanceCenterOfWeightFrontAxle();

    /**
     * Gets the longitudinal weight transfer
     * @return The longitudinal weight transfer
     */
    public double getLongitudinalWeightTransfer();

    /**
     * Gets the lateral weight transfer
     * @return The lateral weight transfer
     */
    public double getLateralWeightTransfer();

    /**
     * Determines on which of the axles the wheel sits and
     * returns the appropiate distance to the center of weight on the longitudinal axis
     *
     * @param wheel The wheel
     * @return The longitudinal distance to the center of weight
     */
    public default double getDistanceToCenterOfWeightLongitudinally(Wheel wheel) {
        if (isFrontWheel(wheel)) {
            return getDistanceCenterOfWeightFrontAxle();
        } else {
            return getDistanceCenterOfWeightRearAxle();
        }
    }

    /**
     * Gets the load on wheel
     * @param wheel The wheel
     * @return The load
     */
    public default double getLoadOnWheel(Wheel wheel, double carWeight, int isAgilityActive, double wheelBase) {
        if (isAgilityActive > 0) {
            carWeight *= 2;
        }

        if (isFrontWheel(wheel)) {
            double weightOnAxle = (carWeight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase  - getLongitudinalWeightTransfer()) / 2;
            if (isFrontLeftWheel(wheel)) {
                return weightOnAxle - (getLateralWeightTransfer() / 2);
            } else {
                return weightOnAxle + (getLateralWeightTransfer() / 2);
            }
        } else {
            double weightOnAxle = (carWeight * getDistanceToCenterOfWeightLongitudinally(wheel) / wheelBase + getLongitudinalWeightTransfer()) / 2;
            if (isBackLeftWheel(wheel)) {
                return weightOnAxle - (getLateralWeightTransfer() / 2);
            } else {
                return weightOnAxle + (getLateralWeightTransfer() / 2);
            }
        }
    }
}
