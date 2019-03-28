package com.battlezone.megamachines.math;

/**
 * A class to provide mathematical helper functions.
 *
 * @author Kieran, Hamzah
 */
public class MathUtils {

    /**
     * Conversion rate from meters per second to miles per hour.
     */
    private static final double MS_TO_MPH = 2.2369362912d;

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
     * A method to clamp a given integer between two limits.
     *
     * @param value      The value to clamp.
     * @param lowerBound The lowest possible value.
     * @param upperBound The highest possible value.
     * @return The clamped value.
     */
    public static int clamp(int value, int lowerBound, int upperBound) {
        return value > upperBound ? upperBound : value < lowerBound ? lowerBound : value;
    }

    /**
     * A method to wrap a given value around a range.
     *
     * @param value      The value to wrap.
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     * @return The wapped value, can be equal to lower, not to upper.
     */
    public static int wrap(int value, int lowerBound, int upperBound) {
        assert lowerBound <= upperBound;
        // Default case
        if (lowerBound == 0) return value < lowerBound ? upperBound - ((-value) % upperBound) : value % upperBound;
            // Shift bounds so that lower is on 0
        else return wrap(value - lowerBound, 0, upperBound - lowerBound) + lowerBound;
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
     * Linearly interpolates between a position and a target
     *
     * @param position
     * @param target
     * @param interpolation
     * @return
     */
    public static float lerpVelocity(float position, float target, float interpolation) {
        return (target - position) * interpolation;
    }

    /**
     * A method to convert from metres per second to miles per hour.
     *
     * @param metresPerSecond The value in ms.
     * @return The value in mph.
     */
    public static double msToMph(double metresPerSecond) {
        return metresPerSecond * MS_TO_MPH;
    }

    /**
     * A method to convert from seconds to nanoseconds.
     *
     * @param seconds The value in seconds.
     * @return The value in nanoseconds.
     */
    public static double secToNan(double seconds) {
        return seconds * 1000000000;
    }

    /**
     * A method to convert from nanoseconds to seconds.
     *
     * @param nanoseconds The value in nanoseconds.
     * @return The value in seconds.
     */
    public static double nanToSec(double nanoseconds) {
        return nanoseconds / 1000000000;
    }

    /**
     * A method to calculate the distance squared between two points.
     *
     * @param x1 The X coordinate of the first point.
     * @param y1 The Y coordinate of the first point.
     * @param x2 The X coordinate of the second point.
     * @param y2 The Y coordinate of the second point.
     * @return The squared distance between the two points.
     */
    public static float distanceSquared(float x1, float y1, float x2, float y2) {
        var xDist = x1 - x2;
        var yDist = y1 - y2;
        var xDistSq = xDist * xDist;
        var yDistSq = yDist * yDist;
        return xDistSq + yDistSq;
    }

    /**
     * A method to calculate the exact distance between two points.
     *
     * @param x1 The X coordinate of the first point.
     * @param y1 The Y coordinate of the first point.
     * @param x2 The X coordinate of the second point.
     * @param y2 The Y coordinate of the second point.
     * @return The exact distance between the two points.
     */
    public static float pythagoras(float x1, float y1, float x2, float y2) {
        var distanceSquared = distanceSquared(x1, y1, x2, y2);
        return (float) Math.sqrt(distanceSquared);
    }
}
