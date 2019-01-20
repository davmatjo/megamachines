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

    public Matrix4f() {
        m00 = m11 = m22 = m33 = 1.0f;
        m01 = m02 = m03 = m10 = m12 = m13 = m20 = m21 = m23 = m30 = m31 = m32 = 0.0f;
    }

    private Matrix4f(float _m00, float _m01, float _m02, float _m03,
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

    public static Matrix4f rotate(float angle) {
        final float r = (float) Math.toRadians(angle);
        final float cos = (float) Math.cos(r);
        final float sin = (float) Math.sin(r);
        return new Matrix4f(cos, sin, 0.0f, 0.0f,
                -sin, cos, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static Matrix4f scale(float scale) {
        return new Matrix4f(scale, 0.0f, 0.0f, 0.0f,
                0.0f, scale, 0.0f, 0.0f,
                0.0f, 0.0f, scale, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {
        return new Matrix4f(2.0f / (right - left), 0.0f, 0.0f, 0.0f,
                0.0f, 2.0f / (top - bottom), 0.0f, 0.0f,
                0.0f, 0.0f, 2.0f / (near - far), 0.0f,
                (left + right) / (left - right), (bottom + top) / (bottom - top), (far + near) / (far - near), 0.0f);
    }

    public Matrix4f translate(Vector3f offset) {
        return translate(offset.x, offset.y, offset.z);
    }

    public Matrix4f translate(Vector2f offset, float z) {
        return translate(offset.x, offset.y, z);
    }

    public Matrix4f translate(float x, float y, float z) {
        return new Matrix4f(m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m00 * x + m10 * y + m20 * z + m30, m01 * x + m11 * y + m21 * z + m31, m02 * x + m12 * y + m22 * z + m32, m03 * x + m13 * y + m23 * z + m33);
    }

    public void get(FloatBuffer buffer) {
        buffer.put(new float[]{m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33});
    }

    public org.joml.Matrix4f toJolm() {
        return new org.joml.Matrix4f(m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33);
    }

}
