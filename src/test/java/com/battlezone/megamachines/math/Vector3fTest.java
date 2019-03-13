package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

public class Vector3fTest {

    @Test
    public void vector3fSelfEqualityTest() {
        final Vector3f v = new Vector3f(0, 0, 0);
        Assert.assertTrue(v.equals(v));
    }

    @Test
    public void vector3fEqualityTest() {
        final Vector3f v1 = new Vector3f(0, 0, 0),
                v2 = new Vector3f(0, 0, 0);
        Assert.assertTrue(v1.equals(v2));
    }

    @Test
    public void vector3fObjectInequalityTest() {
        final Vector3f v = new Vector3f(0, 0, 0);
        final Matrix4f m = new Matrix4f();
        Assert.assertFalse(v.equals(m));
    }

    @Test
    public void vector3fByteArrayTest() {
        final Vector3f v1 = new Vector3f(1.25f, 2.5f, 3.75f);
        final byte[] b = v1.toByteArray();
        final Vector3f v2 = Vector3f.fromByteArray(b, 0);
        Assert.assertEquals(v1, v2);
    }

    @Test
    public void vector3fSetTest() {
        final Vector3f v = new Vector3f(0, 0, 0);
        v.set(1, 2, 3);
        Assert.assertEquals(1, v.x, 0);
        Assert.assertEquals(2, v.y, 0);
    }

    @Test
    public void vector3fAddTest() {
        final Vector3f v = new Vector3f(10, 20, 30);
        v.add(300, 200, 100);
        Assert.assertEquals(310, v.x, 0);
        Assert.assertEquals(220, v.y, 0);
        Assert.assertEquals(130, v.z, 0);
    }

    @Test
    public void vector3fToStringTest() {
        final Vector3f v = new Vector3f(1.25f, 9f, 0.4f);
        Assert.assertEquals("[ \t1.25 \t9.0 \t0.4 \t]", v.toString());
    }

    @Test
    public void vector3fHashcodeTest() {
        final Vector3f v1 = new Vector3f(1, 2, 3),
                v2 = new Vector3f(1, 2, 3),
                v3 = new Vector3f(2, 3, 4);
        Assert.assertEquals(v1.hashCode(), v2.hashCode());
        Assert.assertNotEquals(v1.hashCode(), v3.hashCode());
    }

}
