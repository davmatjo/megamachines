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
     * Generate a random number between min and max, the result is < max and ≥ min
     * @param min The minimum value, inclusive
     * @param max The maximum value, exclusive
     * @return The random number
     */
    public static int randomInteger(int min, int max) {
        return ((int) Math.floor(Math.random() * (max - min))) + min;
    }

}
