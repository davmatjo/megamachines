package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

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

}
