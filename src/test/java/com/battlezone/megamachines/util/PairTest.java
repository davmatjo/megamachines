package com.battlezone.megamachines.util;

import org.junit.Assert;
import org.junit.Test;

public class PairTest {

    @Test
    public void pairEquality() {
        final Pair<Integer, Double> p1 = new Pair<>(1, 5.5),
                p2 = new Pair<>(1, 5.5),
                p3 = new Pair<>(1, 5.51);
        final String s = "A string";
        Assert.assertEquals(p1, p2);
        Assert.assertNotEquals(p1, p3);
        Assert.assertNotEquals(p1, s);
    }

    @Test
    public void pairHashcode() {
        final Pair<Integer, Double> p1 = new Pair<>(1, 5.5),
                p2 = new Pair<>(1, 5.5),
                p3 = new Pair<>(1, 5.51);
        Assert.assertEquals(p1.hashCode(), p2.hashCode());
        Assert.assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    public void pairGetters() {
        final int first = 1, second = 2;
        final Pair<Integer, Integer> p = new Pair<>(first, second);
        Assert.assertEquals(first, p.getFirst().intValue());
        Assert.assertEquals(second, p.getSecond().intValue());
    }

    @Test
    public void pairSetters() {
        final int first = 1, second = 2,
                first_ = 3, second_ = 4;
        final Pair<Integer, Integer> p = new Pair<>(first, second);
        Assert.assertEquals(first, p.getFirst().intValue());
        Assert.assertEquals(second, p.getSecond().intValue());

        p.setFirst(first_);
        Assert.assertEquals(first_, p.getFirst().intValue());

        p.setSecond(second_);
        Assert.assertEquals(second_, p.getSecond().intValue());

        p.set(first, second);
        Assert.assertEquals(first, p.getFirst().intValue());
        Assert.assertEquals(second, p.getSecond().intValue());

    }

    @Test
    public void pairToString() {
        final Pair<Integer, Integer> p = new Pair<>(1, 2);
        Assert.assertEquals("(1, 2)", p.toString());
    }

}
