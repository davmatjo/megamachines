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


    //TODO: Squish matrix, copy from renderer

//    public static void translate(float x, float y, float z, Matrix4f dest) {
//        return new Matrix4f(m00, m01, m02, m03,
//                m10, m11, m12, m13,
//                m20, m21, m22, m23,
//                m00 * x + m10 * y + m20 * z + m30, m01 * x + m11 * y + m21 * z + m31, m02 * x + m12 * y + m22 * z + m32, m03 * x + m13 * y + m23 * z + m33);
//    }

    /**
     * @see #translate(float, float, float, Matrix4f)
     */
    public static Matrix4f translate(Vector3f offset, Matrix4f dest) {
        return translate(offset.x, offset.y, offset.z, dest);
    }

    /**
     * @see #translate(float, float, float, Matrix4f)
     */
    public static Matrix4f translate(Vector2f offset, float z, Matrix4f dest) {
        return translate(offset.x, offset.y, z, dest);
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
    public static Matrix4f translate(float x, float y, float z, Matrix4f dest) {
        dest.m00 = dest.m11 = dest.m22 = dest.m33 = 1.0f;
        dest.m01 = dest.m02 = dest.m03 = dest.m10 = dest.m12 = dest.m13 = dest.m20 = dest.m21 = dest.m23 = 0.0f;
        dest.m30 = x;
        dest.m31 = y;
        dest.m32 = z;
        return dest;
    }

    /**
     * Creates a rotation matrix in the Z axis for the given angle.
     *
     * @param angle angle in degrees.
     * @param dest  the destination matrix to store the result in.
     */
    public static Matrix4f rotateZ(float angle, Matrix4f dest) {
        final double r = Math.toRadians(angle);
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
