package com.battlezone.megamachines.math;

import java.util.Objects;

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
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Vector2f) {
            final Vector2f v1 = (Vector2f) obj;
            return x == v1.x && y == v1.y;
        }
        return false;
    }

    /**
     * Generate a hash code of this vector, considers the values of the vector themselves rather than the object.
     *
     * @return the hash code of this vector.
     */
    @Override
    public int hashCode() {
        // Generate a hash code considering all of the vector values, required so that the default hashCode method isn't
        // used, so that two identical vectors are hashed the same.
        return Objects.hash(x, y);
    }

}
