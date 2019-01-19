package com.battlezone.megamachines.math;

/**
 * A class to provide mathematical helper functions.
 *
 * @author Kieran
 */
public class MathUtils {

    /**
     * A method to clamp a given double value between two limits.
     *
     * @param value      The value to clamp.
     * @param lowerBound The lowest possible value.
     * @param upperBound The highest possible value.
     * @return The clamped value.
     */
    public static double clampd(double value, double lowerBound, double upperBound) {
        return value > upperBound ? upperBound : value < lowerBound ? lowerBound : value;
    }

    /**
     * A method to determine whether a given value is in the specified range.
     *
     * @param value      The value to check.
     * @param lowerBound The lowest possible value.
     * @param upperBound The highest possible value.
     * @return Whether the value is in the range or not.
     */
    public static boolean inRange(int value, int lowerBound, int upperBound) {
        return value >= lowerBound && value <= upperBound;
    }

}
