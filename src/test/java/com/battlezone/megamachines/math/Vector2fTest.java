package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

public class Vector2fTest {

    @Test
    public void vector2fSelfEqualityTest() {
        final Vector2f v = new Vector2f(0, 0);
        Assert.assertTrue(v.equals(v));
    }

    @Test
    public void vector2fEqualityTest() {
        final Vector2f v1 = new Vector2f(0, 0),
                v2 = new Vector2f(0, 0);
        Assert.assertTrue(v1.equals(v2));
    }

    @Test
    public void vector2fObjectInequalityTest() {
        final Vector2f v = new Vector2f(0, 0);
        final Matrix4f m = new Matrix4f();
        Assert.assertFalse(v.equals(m));
    }

    @Test
    public void vector2fSetTest() {
        final Vector2f v = new Vector2f(0, 0);
        v.set(1, 2);
        Assert.assertEquals(1, v.x, 0);
        Assert.assertEquals(2, v.y, 0);
    }

    @Test
    public void vector2fAddTest() {
        final Vector2f v = new Vector2f(10, 20);
        v.add(100, 300);
        Assert.assertEquals(110, v.x, 0);
        Assert.assertEquals(320, v.y, 0);
    }

    @Test
    public void vector2fToStringTest() {
        final Vector2f v = new Vector2f(1.25f, 9f);
        Assert.assertEquals("[ \t1.25 \t9.0 \t]", v.toString());
    }

    @Test
    public void vector2fHashcodeTest() {
        final Vector2f v1 = new Vector2f(1, 2),
                v2 = new Vector2f(1, 2),
                v3 = new Vector2f(2, 3);
        Assert.assertEquals(v1.hashCode(), v2.hashCode());
        Assert.assertNotEquals(v1.hashCode(), v3.hashCode());
    }

}
