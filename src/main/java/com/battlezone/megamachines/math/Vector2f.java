package com.battlezone.megamachines.math;

/**
 * A 2D vector class for floats.
 *
 * @author Kieran
 */
public class Vector2f {

    public float x, y;

    /**
     * Creates a 2D vector with the set values.
     *
     * @param x the X value.
     * @param y the Y value.
     */
    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the values of the vector to the given values.
     *
     * @param x the X value to set.
     * @param y the Y value to set.
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Adds values to the vector.
     *
     * @param x the X value to add.
     * @param y the Y value to add.
     */
    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
    }

}
