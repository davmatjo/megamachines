package com.battlezone.megamachines.math;

import java.nio.FloatBuffer;

/**
 * A 4x4 matrix class for floats.
 *
 * @author Kieran
 */
public class Matrix4f {

    float m00, m01, m02, m03,
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
     * Creates a scaling matrix.
     *
     * @param scale the scale.
     * @return the scaling matrix.
     */
    public static Matrix4f scale(float scale) {
        return new Matrix4f(scale, 0.0f, 0.0f, 0.0f,
                0.0f, scale, 0.0f, 0.0f,
                0.0f, 0.0f, scale, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates an orthographic projection transformation matrix.
     *
     * @param left   the distance from the left frustum edge to the center.
     * @param right  the distance from the right frustum edge to the center.
     * @param bottom the distance from the bottom frustum edge to the center.
     * @param top    the distance from the top frustum edge to the center.
     * @return the orthographic projection transformation matrix.
     */
    public static Matrix4f orthographic(float left, float right, float bottom, float top) {
        return new Matrix4f(2.0f / (right - left), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / (top - bottom), 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f, 0.0f,
                (left + right) / (left - right), (bottom + top) / (bottom - top), 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix in the Z axis for the given angle.
     *
     * @param angle angle in degrees.
     * @return the Z rotation matrix for the angle.
     */
    public static Matrix4f rotateZ(float angle) {
        final double r = Math.toRadians(angle);
        final float cos = (float) Math.cos(r);
        final float sin = (float) Math.sin(r);
        return new Matrix4f(cos, -sin, 0.0f, 0.0f,
                sin, cos, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
    }

    //TODO: Squish matrix, copy from renderer

    /**
     * @see #translate(float, float, float)
     */
    public Matrix4f translate(Vector3f offset) {
        return translate(offset.x, offset.y, offset.z);
    }

    /**
     * @see #translate(float, float, float)
     */
    public Matrix4f translate(Vector2f offset, float z) {
        return translate(offset.x, offset.y, z);
    }

    /**
     * Translates the matrix, by the given values, as a new matrix.
     *
     * @param x the x value.
     * @param y the y value.
     * @param z the z value.
     * @return The translated matrix.
     */
    public Matrix4f translate(float x, float y, float z) {
        return new Matrix4f(m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m00 * x + m10 * y + m20 * z + m30, m01 * x + m11 * y + m21 * z + m31, m02 * x + m12 * y + m22 * z + m32, m03 * x + m13 * y + m23 * z + m33);
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


}
