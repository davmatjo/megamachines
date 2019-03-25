package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

public class MathTest {

    // pythagoras tests

    @Test
    public void pythagoras() {
        var distance = MathUtils.pythagoras(10, 10, 13, 14);
        Assert.assertEquals(distance, 5, 0.01);
    }

    // clampd tests

    @Test
    public void clampdLowerTest() {
        final double lower = 10.7d, upper = 20.21d, value1 = -3.3d, value2 = lower,
                result1 = MathUtils.clampd(value1, lower, upper),
                result2 = MathUtils.clampd(value2, lower, upper);
        Assert.assertEquals(lower, result1, 0);
        Assert.assertEquals(lower, result2, 0);
    }

    @Test
    public void clampdInBoundsTest() {
        final double lower = 0, upper = 20.38d, value = 5.61d,
                result = MathUtils.clampd(value, lower, upper);
        Assert.assertEquals(value, result, 0);
    }

    @Test
    public void clampdUpperTest() {
        final double lower = -30.4d, upper = 28.1d, value1 = upper, value2 = 315.2182d,
                result1 = MathUtils.clampd(value1, lower, upper),
                result2 = MathUtils.clampd(value2, lower, upper);
        Assert.assertEquals(upper, result1, 0);
    }

    // Clamp tests

    @Test
    public void clampBelowLowerTest() {
        final int lower = 10, upper = 20, value = -3,
                result = MathUtils.clamp(value, lower, upper);
        Assert.assertEquals(lower, result);
    }

    @Test
    public void clampOnLowerTest() {
        final int lower = 10, upper = 20, value = lower,
                result = MathUtils.clamp(value, lower, upper);
        Assert.assertEquals(lower, result);
    }

    @Test
    public void clampInBoundsTest() {
        final int lower = 0, upper = 20, value = 5,
                result = MathUtils.clamp(value, lower, upper);
        Assert.assertEquals(value, result);
    }

    @Test
    public void clampOnUpperTest() {
        final int lower = -30, upper = 28, value = upper,
                result = MathUtils.clamp(value, lower, upper);
        Assert.assertEquals(upper, result);
    }

    @Test
    public void clampOverUpperTest() {
        final int lower = 0, upper = 20, value = 25,
                result = MathUtils.clamp(value, lower, upper);
        Assert.assertEquals(upper, result);
    }

    // Wrap tests

    @Test
    public void wrapZeroToLimitTest() {
        // Values: 0 - > 9, then wrap
        final int lower = 0, upper = 10, value1 = 10, value2 = 300, value3 = 409, value4 = -1, value5 = -9, value6 = -11,
                result1 = MathUtils.wrap(value1, lower, upper),
                result2 = MathUtils.wrap(value2, lower, upper),
                result3 = MathUtils.wrap(value3, lower, upper),
                result4 = MathUtils.wrap(value4, lower, upper),
                result5 = MathUtils.wrap(value5, lower, upper),
                result6 = MathUtils.wrap(value6, lower, upper);
        Assert.assertEquals(0, result1);
        Assert.assertEquals(0, result2);
        Assert.assertEquals(9, result3);
        Assert.assertEquals(9, result4);
        Assert.assertEquals(1, result5);
        Assert.assertEquals(9, result6);
    }

    @Test
    public void wrapShiftedBoundsTest() {
        // Values: -9 -> 9, then wrap
        final int lower = -9, upper = 10, value1 = 10, value2 = -11, value3 = 409, value4 = -1, value5 = -9, value6 = 9,
                result1 = MathUtils.wrap(value1, lower, upper),
                result2 = MathUtils.wrap(value2, lower, upper),
                result3 = MathUtils.wrap(value3, lower, upper),
                result4 = MathUtils.wrap(value4, lower, upper),
                result5 = MathUtils.wrap(value5, lower, upper),
                result6 = MathUtils.wrap(value6, lower, upper);
        Assert.assertEquals(-9, result1);
        Assert.assertEquals(8, result2);
        Assert.assertEquals(-9, result3);
        Assert.assertEquals(-1, result4);
        Assert.assertEquals(-9, result5);
        Assert.assertEquals(9, result6);
    }

    @Test
    public void rangeOutOfRangeTest() {
        final int lower = 0, upper = 100, value1 = -1, value2 = -213792, value3 = 101, value4 = 219381;
        final boolean result1 = MathUtils.inRange(value1, lower, upper),
                result2 = MathUtils.inRange(value2, lower, upper),
                result3 = MathUtils.inRange(value3, lower, upper),
                result4 = MathUtils.inRange(value4, lower, upper);
        Assert.assertFalse(result1);
        Assert.assertFalse(result2);
        Assert.assertFalse(result3);
        Assert.assertFalse(result4);
    }

    @Test
    public void rangeInRangeTest() {
        final int lower = -100, upper = 100, value1 = -100, value2 = -47, value3 = 47, value4 = 100;
        final boolean result1 = MathUtils.inRange(value1, lower, upper),
                result2 = MathUtils.inRange(value2, lower, upper),
                result3 = MathUtils.inRange(value3, lower, upper),
                result4 = MathUtils.inRange(value4, lower, upper);
        Assert.assertTrue(result1);
        Assert.assertTrue(result2);
        Assert.assertTrue(result3);
        Assert.assertTrue(result4);
    }

    @Test
    public void randomIntegerTest() {
        final int min = 0, max = 2;
        for (int i=0; i < 10000; i++) {
            int random = MathUtils.randomInteger(min, max);
            Assert.assertTrue(random >= min);
            Assert.assertTrue(random < max);
        }
    }

    @Test
    public void distanceSquaredTest() {
        final double x1 = 12.6, y1 = 63.51,
                x2 = 83.2, y2 = -1.4;
        Assert.assertEquals(9197, MathUtils.distanceSquared(x1, y1, x2, y2), 1);
    }

    @Test
    public void distanceSquaredTestFloat() {
        final float x1 = 12.6f, y1 = 63.51f,
                x2 = 83.2f, y2 = -1.4f;
        Assert.assertEquals(9197f, MathUtils.distanceSquared(x1, y1, x2, y2), 1);
    }

    @Test
    public void nanosecondConversion() {
        final double sec = 2.89d,
                nan = 2890000000d;
        Assert.assertEquals(sec, MathUtils.nanToSec(nan), 0);
        Assert.assertEquals(nan, MathUtils.secToNan(sec), 0);
    }

    @Test
    public void msToMphTest() {
        final double ms = 50d,
                mph = 111.847d;
        Assert.assertEquals(mph, MathUtils.msToMph(ms), 0.05);
    }

    @Test
    public void lerpVelocityTest() {
        final float start = 1, end = 2, interpolation = 0.75f,
                expected = 1.75f,
                actual = start + MathUtils.lerpVelocity(start, end, interpolation);
        Assert.assertEquals(expected, actual, 0);
    }

}
