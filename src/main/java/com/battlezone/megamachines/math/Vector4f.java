package com.battlezone.megamachines.math;

import java.nio.ByteBuffer;
import java.util.Objects;

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

    public static Vector3f fromByteArray(byte[] data, int startIndex) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        return new Vector3f(byteBuffer.getFloat(startIndex), byteBuffer.getFloat(4 + startIndex), byteBuffer.getFloat(8 + startIndex));
    }

    public byte[] toByteArray() {
        return ByteBuffer.allocate(12).putFloat(x).putFloat(y).putFloat(z).array();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Vector4f) {
            final Vector4f v1 = (Vector4f) obj;
            return x == v1.x && y == v1.y && z == v1.z && w == v1.w;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[ \t" + x + " \t" + y + " \t" + z + " \t" + w + " \t]";
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
        return Objects.hash(x, y, z, w);
    }

}
