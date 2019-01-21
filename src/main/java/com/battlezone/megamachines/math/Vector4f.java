package com.battlezone.megamachines.math;

/**
 * A 4D vector class for floats.
 *
 * @author Kieran
 */
public class Vector4f {

    public float x, y, z, w;

    /**
     * Creates a 4D vector with the set values.
     *
     * @param x the X value.
     * @param y the Y value.
     * @param z the Z value.
     * @param w the W value.
     */
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Creates a 4D vector from a 3D vector and a given W value.
     *
     * @param vec the 3D vector to base it on.
     * @param w   the W value.
     */
    public Vector4f(Vector3f vec, float w) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = w;
    }

    /**
     * Sets the values of the vector to the given values.
     *
     * @param x the X value to set.
     * @param y the Y value to set.
     * @param z the Z value to set.
     * @param w the W value to set.
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Adds values to the vector.
     *
     * @param x the X value to add.
     * @param y the Y value to add.
     * @param z the Z value to add.
     * @param w the W value to add.
     */
    public void add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
    }

}
