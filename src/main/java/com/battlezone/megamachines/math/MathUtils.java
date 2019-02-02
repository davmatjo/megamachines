package com.battlezone.megamachines.math;

/**
 * A class to provide mathematical helper functions.
 *
 * @author Kieran, Hamzah
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
     * A method to clamp a given float value between two limits.
     *
     * @param value      The value to clamp.
     * @param lowerBound The lowest possible value.
     * @param upperBound The highest possible value.
     * @return The clamped value.
     */
    public static double clampf(float value, float lowerBound, float upperBound) {
        return value > upperBound ? upperBound : value < lowerBound ? lowerBound : value;
    }

    /**
     * Generate a random number between min and max, the result is < max and ≥ min
     *
     * @param min The minimum value, inclusive
     * @param max The maximum value, exclusive
     * @return The random number
     */
    public static int randomInteger(int min, int max) {
        return ((int) Math.floor(Math.random() * (max - min))) + min;
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

    /**
     * Calculate the squared distance between two points
     *
     * @param x1 X value of first position.
     * @param y1 Y value of first position.
     * @param x2 X value of second position.
     * @param y2 Y value of second position.
     * @return the squared distance.
     */
    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        final double x = x1 - x2;
        final double y = y1 - y2;
        return (x * x) + (y * y);
    }

    /**
     * Calculate the squared distance between two points
     *
     * @param x1 X value of first position.
     * @param y1 Y value of first position.
     * @param x2 X value of second position.
     * @param y2 Y value of second position.
     * @return the squared distance.
     */
    public static float distanceSquared(float x1, float y1, float x2, float y2) {
        final float x = x1 - x2;
        final float y = y1 - y2;
        return (x * x) + (y * y);
    }

}
