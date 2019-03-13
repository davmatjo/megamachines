package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class MatrixTest {

    @Test
    public void matrixSelfEquality() {
        Matrix4f m1 = new Matrix4f(), m2 = new Matrix4f(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        // Check default constructor matrix is equal to itself
        Assert.assertTrue(m1.equals(m1));
        // Check a specified value constructor matrix is equal to itself
        Assert.assertTrue(m2.equals(m2));
    }

    @Test
    public void matrixConstructorEquality() {
        // Identity from default constructor
        Matrix4f identity1 = new Matrix4f();
        // Identity with specified values
        Matrix4f identity2 = new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        Assert.assertTrue(identity1.equals(identity2));
    }

    @Test
    public void matrixEquality() {
        // Check two identity matrices (from constructor) are equal
        Matrix4f i1 = new Matrix4f(), i2 = new Matrix4f();
        Assert.assertTrue(i1.equals(i2));
        // Check that another non-identity matrix isn't equal to an identity matrix
        Matrix4f m1 = new Matrix4f(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Assert.assertFalse(m1.equals(i1));
    }

    @Test
    public void matrixObjectInequality() {
        // Check if a matrix is equal to a vector
        final Matrix4f m = new Matrix4f();
        final Vector3f v = new Vector3f(0, 0, 0);
        Assert.assertFalse(m.equals(v));
    }

    @Test
    public void matrixIdentityMultiplication() {
        Matrix4f m1 = new Matrix4f(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4f m2 = new Matrix4f(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4f i1 = new Matrix4f();
        m1.multiply(i1);
        Assert.assertTrue(m1.equals(m2));
    }

    @Test
    public void matrixIdentityMultiplicationDestination() {
        Matrix4f m1 = new Matrix4f(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
        Matrix4f i1 = new Matrix4f();
        Matrix4f m2 = new Matrix4f();
        m1.multiply(i1, m2);
        Assert.assertTrue(m1.equals(m2));
    }

    @Test
    public void matrixMultiplication() {
        Matrix4f m1 = new Matrix4f(5, 2, 6, 1,
                0, 6, 2, 0,
                3, 8, 1, 4,
                1, 8, 5, 6);
        Matrix4f m2 = new Matrix4f(7, 5, 8, 0,
                1, 8, 2, 6,
                9, 4, 3, 8,
                5, 3, 7, 9);
        Matrix4f expectedResult = new Matrix4f(96, 68, 69, 69,
                24, 56, 18, 52,
                58, 95, 71, 92,
                90, 107, 81, 142);
        Assert.assertTrue(m1.multiply(m2, new Matrix4f()).equals(expectedResult));
    }

    @Test
    public void matrixVectorMultiplication() {
        final Matrix4f m = Matrix4f.translation(10, 20, -30, new Matrix4f());
        final Vector4f v1 = new Vector4f(1, 2, 3, 1),
                v2 = m.multiply(v1, new Vector4f(0, 0, 0, 0));
        Assert.assertEquals(11, v2.x, 0);
        Assert.assertEquals(22, v2.y, 0);
        Assert.assertEquals(-27, v2.z, 0);
    }

    @Test
    public void matrixRotationZ() {
        // Sin 45 = Cos 45 = 0.70710677
        final float rot = 0.70710677f;
        final Matrix4f m = Matrix4f.rotationZ(45, new Matrix4f()),
                expected = new Matrix4f(rot, rot, 0, 0,
                        -rot, rot, 0, 0,
                        0, 0, 1, 0,
                        0, 0, 0, 1);
        Assert.assertEquals(expected, m);
    }

    @Test
    public void matrixTranslationEquality() {
        final float x = 1, y = 2, z = 3;
        Matrix4f translation = Matrix4f.translation(x, y, z, new Matrix4f());
        Matrix4f expectedResult = new Matrix4f(1, 0, 0, x,
                0, 1, 0, y,
                0, 0, 1, z,
                0, 0, 0, 1);
        Assert.assertTrue(translation.equals(expectedResult));
    }

    @Test
    public void matrixTranslationApplication() {
        final float x = 1, y = 2, z = 3;
        Matrix4f translation = Matrix4f.translation(x, y, z, new Matrix4f());
        // (10, 3, 5)
        Matrix4f m1 = new Matrix4f(1, 0, 0, 10,
                0, 1, 0, 3,
                0, 0, 1, 5,
                0, 0, 0, 1);
        Matrix4f result = m1.multiply(translation, new Matrix4f());
        Matrix4f expectedResult = new Matrix4f(1, 0, 0, 10 + x,
                0, 1, 0, 3 + y,
                0, 0, 1, 5 + z,
                0, 0, 0, 1);
        Assert.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void matrixTranslationReverseApplication() {
        // Starting position
        final float sX = 73, sY = 49, sZ = 27;
        // Translation
        final float x1 = 1, y1 = 2, z1 = 3;
        // Inverse translation back to start
        final float x2 = -x1, y2 = -y1, z2 = -z1;
        // Starting matrix
        Matrix4f m1 = new Matrix4f(1, 0, 0, sX,
                0, 1, 0, sY,
                0, 0, 1, sZ,
                0, 0, 0, 1);
        // Create translation
        Matrix4f translation1 = Matrix4f.translation(x1, y1, z1, new Matrix4f());
        // Create inverse translation
        Matrix4f translation2 = Matrix4f.translation(x2, y2, z2, new Matrix4f());
        // Final position should equal start position
        Matrix4f result = m1.multiply(translation1, new Matrix4f()).multiply(translation2);
        Assert.assertTrue(result.equals(m1));
    }

    @Test
    public void matrixTranslationMultiApplications() {
        // Starting position
        final float sX = 73, sY = 49, sZ = 27;
        // First translation
        final float x1 = -7, y1 = 24, z1 = 76;
        // Second translation
        final float x2 = 53, y2 = 47, z2 = 3;
        // Final position
        final float eX = sX + x1 + x2, eY = sY + y1 + y2, eZ = sZ + z1 + z2;
        // Starting matrix
        Matrix4f m1 = new Matrix4f(1, 0, 0, sX,
                0, 1, 0, sY,
                0, 0, 1, sZ,
                0, 0, 0, 1);
        // Create the first translation
        Matrix4f translation1 = Matrix4f.translation(x1, y1, z1, new Matrix4f());
        // Create the second translation
        Matrix4f translation2 = Matrix4f.translation(x2, y2, z2, new Matrix4f());
        // Create the expected matrix
        Matrix4f expectedResult = new Matrix4f(1, 0, 0, eX,
                0, 1, 0, eY,
                0, 0, 1, eZ,
                0, 0, 0, 1);
        // Perform the translations
        Matrix4f result = m1.multiply(translation1, new Matrix4f()).multiply(translation2);
        // Expected outcome
        Assert.assertTrue(result.equals(expectedResult));
    }

    @Test
    public void matrixTranslatingTest() {
        final float x = 1, y = 2, z = 3;
        final Matrix4f start = new Matrix4f(),
                translated = Matrix4f.translate(start, x, y, z, new Matrix4f()),
                expected = new Matrix4f(1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        x, y, z, 1);
        Assert.assertTrue(translated.equals(expected));

        final Matrix4f translated2 = Matrix4f.translate(translated, x, y, z, new Matrix4f()),
                expected2 = new Matrix4f(1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        x + x, y + y, z + z, 1);
        Assert.assertTrue(translated2.equals(expected2));
    }

    @Test
    public void matrixTranslatingVectorTest() {
        final float x = 1, y = 2, z = 3;
        final Vector3f translation = new Vector3f(x, y, z);
        final Matrix4f start = new Matrix4f(),
                translated = Matrix4f.translate(start, translation, new Matrix4f()),
                expected = new Matrix4f(1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        x, y, z, 1);
        Assert.assertTrue(translated.equals(expected));

        final Matrix4f translated2 = Matrix4f.translate(translated, translation, new Matrix4f()),
                expected2 = new Matrix4f(1, 0, 0, 0,
                        0, 1, 0, 0,
                        0, 0, 1, 0,
                        x + x, y + y, z + z, 1);
        Assert.assertTrue(translated2.equals(expected2));
    }

    @Test
    public void matrixDirectAccessTest() {
        // Initial matrix, increasing values
        final Matrix4f m = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16);
        Assert.assertEquals(1, m.m00, 0);
        Assert.assertEquals(2, m.m01, 0);
        Assert.assertEquals(3, m.m02, 0);
        Assert.assertEquals(4, m.m03, 0);
        Assert.assertEquals(5, m.m10, 0);
        Assert.assertEquals(6, m.m11, 0);
        Assert.assertEquals(7, m.m12, 0);
        Assert.assertEquals(8, m.m13, 0);
        Assert.assertEquals(9, m.m20, 0);
        Assert.assertEquals(10, m.m21, 0);
        Assert.assertEquals(11, m.m22, 0);
        Assert.assertEquals(12, m.m23, 0);
        Assert.assertEquals(13, m.m30, 0);
        Assert.assertEquals(14, m.m31, 0);
        Assert.assertEquals(15, m.m32, 0);
        Assert.assertEquals(16, m.m33, 0);
    }

    @Test
    public void matrixMethodAccessTest() {
        // Initial matrix, increasing values
        final Matrix4f m = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16);
        Assert.assertEquals(1, m.m00(), 0);
        Assert.assertEquals(2, m.m01(), 0);
        Assert.assertEquals(3, m.m02(), 0);
        Assert.assertEquals(4, m.m03(), 0);
        Assert.assertEquals(5, m.m10(), 0);
        Assert.assertEquals(6, m.m11(), 0);
        Assert.assertEquals(7, m.m12(), 0);
        Assert.assertEquals(8, m.m13(), 0);
        Assert.assertEquals(9, m.m20(), 0);
        Assert.assertEquals(10, m.m21(), 0);
        Assert.assertEquals(11, m.m22(), 0);
        Assert.assertEquals(12, m.m23(), 0);
        Assert.assertEquals(13, m.m30(), 0);
        Assert.assertEquals(14, m.m31(), 0);
        Assert.assertEquals(15, m.m32(), 0);
        Assert.assertEquals(16, m.m33(), 0);
    }

    @Test
    public void matrixSetMethodTest() {
        final Matrix4f expected = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16);
        Matrix4f actual = new Matrix4f();
        actual.m00(1);
        actual.m01(2);
        actual.m02(3);
        actual.m03(4);
        actual.m10(5);
        actual.m11(6);
        actual.m12(7);
        actual.m13(8);
        actual.m20(9);
        actual.m21(10);
        actual.m22(11);
        actual.m23(12);
        actual.m30(13);
        actual.m31(14);
        actual.m32(15);
        actual.m33(16);

        Assert.assertTrue(expected.equals(actual));
    }

    @Test
    public void matrixScaleTest() {
        final float scale = 5f;
        final Matrix4f expected = new Matrix4f(scale, 0, 0, 0,
                0, scale, 0, 0,
                0, 0, scale, 0,
                0, 0, 0, 1);
        final Matrix4f actual = Matrix4f.scale(5, new Matrix4f());
        Assert.assertTrue(expected.equals(actual));
    }

    @Test
    public void matrixIndividualScaleTest() {
        final float scaleX = 5f, scaleY = 7f, scaleZ = -3f;
        final Matrix4f expected = new Matrix4f(scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1);
        final Matrix4f actual = Matrix4f.scale(scaleX, scaleY, scaleZ, new Matrix4f());
        Assert.assertTrue(expected.equals(actual));
    }

    @Test
    public void matrixToStringTest() {
        final Matrix4f identity = new Matrix4f();
        final String expectedIdentity = "1.0 0.0 0.0 0.0\n0.0 1.0 0.0 0.0\n0.0 0.0 1.0 0.0\n0.0 0.0 0.0 1.0";
        Assert.assertEquals(expectedIdentity, identity.toString());

        final Matrix4f increase = new Matrix4f(1.5f, 2.25f, 3.5f, 4.25f,
                5.5f, 6.25f, 7.5f, 8.25f,
                9.5f, 10.25f, 11.5f, 12.25f,
                13.5f, 14.25f, 15.5f, 16.25f);
        final String expectedIncrease = "1.5 2.25 3.5 4.25\n5.5 6.25 7.5 8.25\n9.5 10.25 11.5 12.25\n13.5 14.25 15.5 16.25";
        Assert.assertEquals(expectedIncrease, increase.toString());
    }

    @Test
    public void matrixOrthographicTest() {
        final float left = -2, right = 2, top = 1, bottom = -1, near = -1, far = 1;
        final Matrix4f ortho = Matrix4f.orthographic(left, right, bottom, top, new Matrix4f()),
                // Calculate it by hand using the correct formula
                expected = new Matrix4f(2 / (right - left), 0, 0, -((right + left) / (right - left)),
                        0, 2 / (top - bottom), 0, -((top + bottom) / (top - bottom)),
                        0, 0, -2 / (far - near), -((far + near) / (far - near)),
                        0, 0, 0, 1);
        Assert.assertEquals(expected, ortho);
    }

    @Test
    public void matrixHashcodeTest() {
        final Matrix4f m1 = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16),
                m2 = new Matrix4f(1, 2, 3, 4,
                        5, 6, 7, 8,
                        9, 10, 11, 12,
                        13, 14, 15, 16);
        Assert.assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    public void matrixHashcodeDifferentTest() {
        final Matrix4f m1 = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16),
                m2 = new Matrix4f(0, 1, 2, 3,
                        4, 5, 6, 7,
                        8, 9, 10, 11,
                        12, 13, 14, 15);
        Assert.assertNotEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    public void matrixCloneValueEqualityTest() {
        final Matrix4f m1 = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16),
                m2 = m1.clone();
        Assert.assertEquals(m1, m2);
    }

    @Test
    public void matrixCloneReferenceTest() {
        final Matrix4f m1 = new Matrix4f(),
                m2 = m1,
                m3 = m1.clone();
        Assert.assertTrue(m1 == m2);
        Assert.assertFalse(m1 == m3);
        Assert.assertFalse(m2 == m3);
    }

    @Test
    public void matrixFloatBufferTest() {
        final Matrix4f m1 = new Matrix4f(1, 2, 3, 4,
                5, 6, 7, 8,
                9, 10, 11, 12,
                13, 14, 15, 16);
        final FloatBuffer buffer = m1.get(BufferUtils.createFloatBuffer(16));
        Assert.assertEquals(1f, buffer.get(0), 0);
        Assert.assertEquals(2f, buffer.get(1), 0);
        Assert.assertEquals(3f, buffer.get(2), 0);
        Assert.assertEquals(4f, buffer.get(3), 0);
        Assert.assertEquals(5f, buffer.get(4), 0);
        Assert.assertEquals(6f, buffer.get(5), 0);
        Assert.assertEquals(7f, buffer.get(6), 0);
        Assert.assertEquals(8f, buffer.get(7), 0);
        Assert.assertEquals(9f, buffer.get(8), 0);
        Assert.assertEquals(10f, buffer.get(9), 0);
        Assert.assertEquals(11f, buffer.get(10), 0);
        Assert.assertEquals(12f, buffer.get(11), 0);
        Assert.assertEquals(13f, buffer.get(12), 0);
        Assert.assertEquals(14f, buffer.get(13), 0);
        Assert.assertEquals(15f, buffer.get(14), 0);
        Assert.assertEquals(16f, buffer.get(15), 0);
    }
}
