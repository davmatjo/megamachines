package com.battlezone.megamachines.math;

/**
 * A 3D vector class for floats.
 *
 * @author Kieran
 */
public class Vector3f {

    public float x, y, z;

    /**
     * Creates a 3D vector with the set values.
     *
     * @param x the X value.
     * @param y the Y value.
     * @param z the Z value.
     */
    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a 3D vector from a 2D vector and a given Z value.
     *
     * @param vec the 2D vector to base it on.
     * @param z   the Z value.
     */
    public Vector3f(Vector2f vec, float z) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = z;
    }

    /**
     * Sets the values of the vector to the given values.
     *
     * @param x the X value to set.
     * @param y the Y value to set.
     * @param z the Z value to set.
     */
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds values to the vector.
     *
     * @param x the X value to add.
     * @param y the Y value to add.
     * @param z the Z value to add.
     */
    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

}
