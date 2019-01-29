package com.battlezone.megamachines.math;

import java.nio.FloatBuffer;
import java.util.Objects;

/**
 * A 4x4 matrix class for floats.
 *
 * @author Kieran
 */
public class Matrix4f {

    public final static Matrix4f IDENTITY = new Matrix4f();

    private float m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33;

    /**
     * Creates a 4x4 identity matrix.
     */
    public Matrix4f() {
        m00 = m11 = m22 = m33 = 1.0f;
        m01 = m02 = m03 = m10 = m12 = m13 = m20 = m21 = m23 = m30 = m31 = m32 = 0.0f;
    }

    /**
     * Creates a 4x4 matrix with specified values.
     *
     * @param _m00 the [0,0] value.
     * @param _m01 the [0,1] value.
     * @param _m02 the [0,2] value.
     * @param _m03 the [0,3] value.
     * @param _m10 the [1,0] value.
     * @param _m11 the [1,1] value.
     * @param _m12 the [1,2] value.
     * @param _m13 the [1,3] value.
     * @param _m20 the [2,0] value.
     * @param _m21 the [2,1] value.
     * @param _m22 the [2,2] value.
     * @param _m23 the [2,3] value.
     * @param _m30 the [3,0] value.
     * @param _m31 the [3,1] value.
     * @param _m32 the [3,2] value.
     * @param _m33 the [3,3] value.
     */
    public Matrix4f(float _m00, float _m01, float _m02, float _m03,
                    float _m10, float _m11, float _m12, float _m13,
                    float _m20, float _m21, float _m22, float _m23,
                    float _m30, float _m31, float _m32, float _m33) {
        this.m00 = _m00;
        this.m01 = _m01;
        this.m02 = _m02;
        this.m03 = _m03;
        this.m10 = _m10;
        this.m11 = _m11;
        this.m12 = _m12;
        this.m13 = _m13;
        this.m20 = _m20;
        this.m21 = _m21;
        this.m22 = _m22;
        this.m23 = _m23;
        this.m30 = _m30;
        this.m31 = _m31;
        this.m32 = _m32;
        this.m33 = _m33;
    }

    /**
     * @see #scale(float, float, float, Matrix4f)
     */
    public static Matrix4f scale(float scale, Matrix4f dest) {
        return scale(scale, scale, scale, dest);
    }

    /**
     * Creates a scaling matrix and puts it in the destination matrix.
     *
     * @param x    the X scale.
     * @param y    the Y scale.
     * @param z    the Z scale.
     * @param dest the destination matrix to store the result in.
     * @return the pointer to the destination matrix.
     */
    public static Matrix4f scale(float x, float y, float z, Matrix4f dest) {
        dest.m00 = x;
        dest.m11 = y;
        dest.m22 = z;
        dest.m33 = 1.0f;
        dest.m01 = dest.m02 = dest.m03 = dest.m10 = dest.m12 = dest.m13 = dest.m20 = dest.m21 = dest.m23 = dest.m30 = dest.m31 = dest.m32 = 0.0f;
        return dest;
    }

    /**
     * Creates an orthographic projection transformation matrix.
     *
     * @param left   the distance from the left frustum edge to the center.
     * @param right  the distance from the right frustum edge to the center.
     * @param bottom the distance from the bottom frustum edge to the center.
     * @param top    the distance from the top frustum edge to the center.
     * @param dest   the destination matrix to store the result in.
     * @return the pointer to the destination matrix.
     */
    public static Matrix4f orthographic(float left, float right, float bottom, float top, Matrix4f dest) {
        dest.m00 = 2.0f / (right - left);
        dest.m01 = dest.m02 = dest.m03 = dest.m10 = dest.m12 = dest.m13 = dest.m20 = dest.m21 = dest.m23 = dest.m32 = 0.0f;
        dest.m11 = 2.0f / (top - bottom);
        dest.m22 = -1.0f;
        dest.m30 = (left + right) / (left - right);
        dest.m31 = (bottom + top) / (bottom - top);
        dest.m33 = 1.0f;
        return dest;
    }

    /**
     * @see #translation(float, float, float, Matrix4f)
     */
    public static Matrix4f translation(Vector3f offset, Matrix4f dest) {
        return translation(offset.x, offset.y, offset.z, dest);
    }

    /**
     * @see #translation(float, float, float, Matrix4f)
     */
    public static Matrix4f translation(Vector2f offset, float z, Matrix4f dest) {
        return translation(offset.x, offset.y, z, dest);
    }

    /**
     * Creates a translation matrix, by the given values.
     *
     * @param x    the x value.
     * @param y    the y value.
     * @param z    the z value.
     * @param dest the destination matrix to store the result in.
     * @return the pointer to the destination matrix.
     */
    public static Matrix4f translation(float x, float y, float z, Matrix4f dest) {
        dest.m00 = dest.m11 = dest.m22 = dest.m33 = 1.0f;
        dest.m01 = dest.m02 = dest.m10 = dest.m12 = dest.m20 = dest.m21 = dest.m30 = dest.m31 = 0.0f;
        dest.m03 = x;
        dest.m13 = y;
        dest.m23 = z;
        return dest;
    }

    /**
     * @see #translate(Matrix4f, float, float, float, Matrix4f)
     */
    public static Matrix4f translate(Matrix4f src, Vector3f offset, Matrix4f dest) {
        return translate(src, offset.x, offset.y, offset.z, dest);
    }

    /**
     * Apply a flipped translation to a given matrix, stored in the given destination.
     *
     * @param src  the matrix to translate.
     * @param x    the x value.
     * @param y    the y value.
     * @param z    the z value.
     * @param dest the destination matrix to store the result in.
     * @return the pointer to the destination matrix.
     */
    public static Matrix4f translate(Matrix4f src, float x, float y, float z, Matrix4f dest) {
        dest.set(src.m00, src.m01, src.m02, src.m03,
                src.m10, src.m11, src.m12, src.m13,
                src.m20, src.m21, src.m22, src.m23,
                src.m00 * x + src.m10 * y + src.m20 * z + src.m30, src.m01 * x + src.m11 * y + src.m21 * z + src.m31, src.m02 * x + src.m12 * y + src.m22 * z + src.m32, src.m03 * x + src.m13 * y + src.m23 * z + src.m33);
        return dest;
    }

    /**
     * Creates a rotation matrix in the Z axis for the given angle.
     *
     * @param angle angle in degrees.
     * @param dest  the destination matrix to store the result in.
     */
    public static Matrix4f rotationZ(float angle, Matrix4f dest) {
        final double r = -Math.toRadians(angle);
        final float cos = (float) Math.cos(r);
        final float sin = (float) Math.sin(r);
        dest.m00 = dest.m11 = cos;
        dest.m01 = -sin;
        dest.m10 = sin;
        dest.m02 = dest.m03 = dest.m12 = dest.m13 = dest.m20 = dest.m21 = dest.m23 = dest.m30 = dest.m31 = dest.m32 = 0.0f;
        dest.m22 = dest.m33 = 1.0f;
        return dest;
    }

    /**
     * Multiplies this matrix by the given matrix.
     *
     * @param m the matrix to multiply by.
     * @return the pointer to this matrix, after the multiplication has been done.
     */
    public Matrix4f multiply(Matrix4f m) {
        set((m00 * m.m00) + (m01 * m.m10) + (m03 * m.m30) + (m02 * m.m20), (m00 * m.m01) + (m01 * m.m11) + (m02 * m.m21) + (m03 * m.m31), (m00 * m.m02) + (m01 * m.m12) + (m02 * m.m22) + (m03 * m.m32), (m00 * m.m03) + (m01 * m.m13) + (m02 * m.m23) + (m03 * m.m33),
                (m.m00 * m10) + (m.m10 * m11) + (m13 * m.m30) + (m12 * m.m20), (m.m01 * m10) + (m.m11 * m11) + (m12 * m.m21) + (m13 * m.m31), (m.m02 * m10) + (m11 * m.m12) + (m12 * m.m22) + (m13 * m.m32), (m.m03 * m10) + (m11 * m.m13) + (m12 * m.m23) + (m13 * m.m33),
                (m.m10 * m21) + (m23 * m.m30) + (m.m00 * m20) + (m22 * m.m20), (m.m11 * m21) + (m.m21 * m22) + (m23 * m.m31) + (m.m01 * m20), (m.m12 * m21) + (m22 * m.m22) + (m23 * m.m32) + (m.m02 * m20), (m.m13 * m21) + (m22 * m.m23) + (m23 * m.m33) + (m.m03 * m20),
                (m.m00 * m30) + (m.m10 * m31) + (m.m30 * m33) + (m32 * m.m20), (m.m01 * m30) + (m.m11 * m31) + (m.m21 * m32) + (m.m31 * m33), (m.m02 * m30) + (m.m12 * m31) + (m.m22 * m32) + (m.m32 * m33), (m.m03 * m30) + (m.m13 * m31) + (m.m23 * m32) + (m33 * m.m33));
        return this;
    }

    /**
     * Multiplies this matrix by the given matrix, storing it in the destination matrix.
     *
     * @param m    the matrix to multiply by.
     * @param dest the destination matrix to store the result in.
     * @return the pointer to this matrix, after the multiplication has been done.
     */
    public Matrix4f multiply(Matrix4f m, Matrix4f dest) {
        dest.set((m00 * m.m00) + (m01 * m.m10) + (m03 * m.m30) + (m02 * m.m20), (m00 * m.m01) + (m01 * m.m11) + (m02 * m.m21) + (m03 * m.m31), (m00 * m.m02) + (m01 * m.m12) + (m02 * m.m22) + (m03 * m.m32), (m00 * m.m03) + (m01 * m.m13) + (m02 * m.m23) + (m03 * m.m33),
                (m.m00 * m10) + (m.m10 * m11) + (m13 * m.m30) + (m12 * m.m20), (m.m01 * m10) + (m.m11 * m11) + (m12 * m.m21) + (m13 * m.m31), (m.m02 * m10) + (m11 * m.m12) + (m12 * m.m22) + (m13 * m.m32), (m.m03 * m10) + (m11 * m.m13) + (m12 * m.m23) + (m13 * m.m33),
                (m.m10 * m21) + (m23 * m.m30) + (m.m00 * m20) + (m22 * m.m20), (m.m11 * m21) + (m.m21 * m22) + (m23 * m.m31) + (m.m01 * m20), (m.m12 * m21) + (m22 * m.m22) + (m23 * m.m32) + (m.m02 * m20), (m.m13 * m21) + (m22 * m.m23) + (m23 * m.m33) + (m.m03 * m20),
                (m.m00 * m30) + (m.m10 * m31) + (m.m30 * m33) + (m32 * m.m20), (m.m01 * m30) + (m.m11 * m31) + (m.m21 * m32) + (m.m31 * m33), (m.m02 * m30) + (m.m12 * m31) + (m.m22 * m32) + (m.m32 * m33), (m.m03 * m30) + (m.m13 * m31) + (m.m23 * m32) + (m33 * m.m33));
        return dest;
    }

    private void set(float _m00, float _m01, float _m02, float _m03,
                     float _m10, float _m11, float _m12, float _m13,
                     float _m20, float _m21, float _m22, float _m23,
                     float _m30, float _m31, float _m32, float _m33) {
        this.m00 = _m00;
        this.m01 = _m01;
        this.m02 = _m02;
        this.m03 = _m03;
        this.m10 = _m10;
        this.m11 = _m11;
        this.m12 = _m12;
        this.m13 = _m13;
        this.m20 = _m20;
        this.m21 = _m21;
        this.m22 = _m22;
        this.m23 = _m23;
        this.m30 = _m30;
        this.m31 = _m31;
        this.m32 = _m32;
        this.m33 = _m33;
    }

    /**
     * Set the [0,0] value for the matrix.
     *
     * @param value
     */
    public void m00(float value) {
        m00 = value;
    }

    /**
     * Set the [1,0] value for the matrix.
     *
     * @param value
     */
    public void m10(float value) {
        m10 = value;
    }

    /**
     * Set the [2,0] value for the matrix.
     *
     * @param value
     */
    public void m20(float value) {
        m20 = value;
    }

    /**
     * Set the [3,0] value for the matrix.
     *
     * @param value
     */
    public void m30(float value) {
        m30 = value;
    }

    /**
     * Set the [0,1] value for the matrix.
     *
     * @param value
     */
    public void m01(float value) {
        m01 = value;
    }

    /**
     * Set the [1,1] value for the matrix.
     *
     * @param value
     */
    public void m11(float value) {
        m11 = value;
    }

    /**
     * Set the [2,1] value for the matrix.
     *
     * @param value
     */
    public void m21(float value) {
        m21 = value;
    }

    /**
     * Set the [3,1] value for the matrix.
     *
     * @param value
     */
    public void m31(float value) {
        m31 = value;
    }

    /**
     * Set the [0,2] value for the matrix.
     *
     * @param value
     */
    public void m02(float value) {
        m02 = value;
    }

    /**
     * Set the [1,2] value for the matrix.
     *
     * @param value
     */
    public void m12(float value) {
        m12 = value;
    }

    /**
     * Set the [2,2] value for the matrix.
     *
     * @param value
     */
    public void m22(float value) {
        m22 = value;
    }

    /**
     * Set the [3,2] value for the matrix.
     *
     * @param value
     */
    public void m32(float value) {
        m32 = value;
    }

    /**
     * Set the [0,3] value for the matrix.
     *
     * @param value
     */
    public void m03(float value) {
        m03 = value;
    }

    /**
     * Set the [1,3] value for the matrix.
     *
     * @param value
     */
    public void m13(float value) {
        m13 = value;
    }

    /**
     * Set the [2,3] value for the matrix.
     *
     * @param value
     */
    public void m23(float value) {
        m23 = value;
    }

    /**
     * Set the [3,3] value for the matrix.
     *
     * @param value
     */
    public void m33(float value) {
        m33 = value;
    }

    /**
     * Get the [0,0] value from the matrix.
     *
     * @return the [0,0] value from the matrix.
     */
    public float m00() {
        return m00;
    }

    /**
     * Get the [1,0] value from the matrix.
     *
     * @return the [1,0] value from the matrix.
     */
    public float m10() {
        return m10;
    }

    /**
     * Get the [2,0] value from the matrix.
     *
     * @return the [2,0] value from the matrix.
     */
    public float m20() {
        return m20;
    }

    /**
     * Get the [3,0] value from the matrix.
     *
     * @return the [3,0] value from the matrix.
     */
    public float m30() {
        return m30;
    }

    /**
     * Get the [0,1] value from the matrix.
     *
     * @return the [0,1] value from the matrix.
     */
    public float m01() {
        return m01;
    }

    /**
     * Get the [1,1] value from the matrix.
     *
     * @return the [1,1] value from the matrix.
     */
    public float m11() {
        return m11;
    }

    /**
     * Get the [2,1] value from the matrix.
     *
     * @return the [2,1] value from the matrix.
     */
    public float m21() {
        return m21;
    }

    /**
     * Get the [3,1] value from the matrix.
     *
     * @return the [3,1] value from the matrix.
     */
    public float m31() {
        return m31;
    }

    /**
     * Get the [0,2] value from the matrix.
     *
     * @return the [0,2] value from the matrix.
     */
    public float m02() {
        return m02;
    }

    /**
     * Get the [1,2] value from the matrix.
     *
     * @return the [1,2] value from the matrix.
     */
    public float m12() {
        return m12;
    }

    /**
     * Get the [2,2] value from the matrix.
     *
     * @return the [2,2] value from the matrix.
     */
    public float m22() {
        return m22;
    }

    /**
     * Get the [3,2] value from the matrix.
     *
     * @return the [3,2] value from the matrix.
     */
    public float m32() {
        return m32;
    }

    /**
     * Get the [0,3] value from the matrix.
     *
     * @return the [0,3] value from the matrix.
     */
    public float m03() {
        return m03;
    }

    /**
     * Get the [1,3] value from the matrix.
     *
     * @return the [1,3] value from the matrix.
     */
    public float m13() {
        return m13;
    }

    /**
     * Get the [2,3] value from the matrix.
     *
     * @return the [2,3] value from the matrix.
     */
    public float m23() {
        return m23;
    }

    /**
     * Get the [3,3] value from the matrix.
     *
     * @return the [3,3] value from the matrix.
     */
    public float m33() {
        return m33;
    }

    /**
     * Dump the matrix into a FloatBuffer.
     *
     * @param buffer the buffer to dump into.
     * @return the buffer.
     */
    public FloatBuffer get(FloatBuffer buffer) {
        buffer.put(0, m00);
        buffer.put(1, m01);
        buffer.put(2, m02);
        buffer.put(3, m03);
        buffer.put(4, m10);
        buffer.put(5, m11);
        buffer.put(6, m12);
        buffer.put(7, m13);
        buffer.put(8, m20);
        buffer.put(9, m21);
        buffer.put(10, m22);
        buffer.put(11, m23);
        buffer.put(12, m30);
        buffer.put(13, m31);
        buffer.put(14, m32);
        buffer.put(15, m33);
        return buffer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Matrix4f) {
            final Matrix4f m1 = (Matrix4f) obj;
            return m00 == m1.m00 && m01 == m1.m01 && m02 == m1.m02 && m03 == m1.m03 &&
                    m10 == m1.m10 && m11 == m1.m11 && m12 == m1.m12 && m13 == m1.m13 &&
                    m20 == m1.m20 && m21 == m1.m21 && m22 == m1.m22 && m23 == m1.m23 &&
                    m30 == m1.m30 && m31 == m1.m31 && m32 == m1.m32 && m33 == m1.m33;
        }
        return false;
    }

    @Override
    public String toString() {
        return m00 + " " + m01 + " " + m02 + " " + m03 + "\n" + m10 + " " + m11 + " " + m12 + " " + m13 + "\n" + m20 + " " + m21 + " " + m22 + " " + m23 + "\n" + m30 + " " + m31 + " " + m32 + " " + m33;
    }

    /**
     * Generate a hash code of this matrix, considers the values of the matrix themselves rather than the object.
     *
     * @return the hash code of this matrix.
     */
    @Override
    public int hashCode() {
        // Generate a hash code considering all of the matrix values, required so that the default hashCode method isn't
        // used, so that two identical matrices are hashed the same.
        return Objects.hash(m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33);
    }

}