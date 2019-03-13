package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

public class Vector4fTest {

    @Test
    public void vector4fSelfEqualityTest() {
        final Vector4f v = new Vector4f(0, 0, 0, 0);
        Assert.assertTrue(v.equals(v));
    }

    @Test
    public void vector4fEqualityTest() {
        final Vector4f v1 = new Vector4f(0, 0, 0, 0),
                v2 = new Vector4f(0, 0, 0, 0);
        Assert.assertTrue(v1.equals(v2));
    }

    @Test
    public void vector4fObjectInequalityTest() {
        final Vector4f v = new Vector4f(0, 0, 0, 0);
        final Matrix4f m = new Matrix4f();
        Assert.assertFalse(v.equals(m));
    }

    @Test
    public void vector4fVector3fConstructorTest() {
        final Vector3f v1 = new Vector3f(1, 2, 3);
        final Vector4f v2 = new Vector4f(v1, 4),
                v3 = new Vector4f(1, 2, 3, 4);
        Assert.assertEquals(v3, v2);
    }

    @Test
    public void vector4fToByteArrayTest() {
        // This is used to transport colours without alpha, so a Vector3f can be made out of it
        final Vector4f v1 = new Vector4f(0.3f, 0.6f, 0.9f, 0.5f);
        final Vector3f v2 = new Vector3f(0.3f, 0.6f, 0.9f);
        final Vector3f v3 = Vector3f.fromByteArray(v1.toByteArray(), 0);
        Assert.assertEquals(v2, v3);
    }

    @Test
    public void vector4fSetTest() {
        final Vector4f v = new Vector4f(0, 0, 0, 0);
        v.set(1, 2, 3, 4);
        Assert.assertEquals(1, v.x, 0);
        Assert.assertEquals(2, v.y, 0);
        Assert.assertEquals(3, v.z, 0);
        Assert.assertEquals(4, v.w, 0);
    }

    @Test
    public void vector4fAddTest() {
        final Vector4f v = new Vector4f(10, 20, 30, 40);
        v.add(400, 300, 200, 100);
        Assert.assertEquals(410, v.x, 0);
        Assert.assertEquals(320, v.y, 0);
        Assert.assertEquals(230, v.z, 0);
        Assert.assertEquals(140, v.w, 0);
    }

    @Test
    public void vector4fToStringTest() {
        final Vector4f v = new Vector4f(1.25f, 9f, 0.4f, 1.59f);
        Assert.assertEquals("[ \t1.25 \t9.0 \t0.4 \t1.59 \t]", v.toString());
    }

    @Test
    public void vector4fHashcodeTest() {
        final Vector4f v1 = new Vector4f(1, 2, 3, 4),
                v2 = new Vector4f(1, 2, 3, 4),
                v3 = new Vector4f(2, 3, 4, 5);
        Assert.assertEquals(v1.hashCode(), v2.hashCode());
        Assert.assertNotEquals(v1.hashCode(), v3.hashCode());
    }

}
