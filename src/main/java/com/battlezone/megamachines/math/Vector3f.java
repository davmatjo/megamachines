package com.battlezone.megamachines.math;

import java.nio.ByteBuffer;
import java.util.Objects;

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

    public static Vector3f fromByteArray(byte[] data, int startIndex) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data, startIndex, 12);
        return new Vector3f(byteBuffer.getFloat(0), byteBuffer.getFloat(4), byteBuffer.getFloat(8));
    }

    public byte[] toByteArray() {
        return ByteBuffer.allocate(12).putFloat(x).putFloat(y).putFloat(z).array();
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Vector3f) {
            final Vector3f v1 = (Vector3f) obj;
            return x == v1.x && y == v1.y && z == v1.z;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[ \t" + x + " \t" + y + " \t" + z + " \t]";
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
        return Objects.hash(x, y, z);
    }

}
