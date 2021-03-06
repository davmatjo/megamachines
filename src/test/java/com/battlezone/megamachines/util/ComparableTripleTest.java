package com.battlezone.megamachines.util;

import org.junit.Assert;
import org.junit.Test;

public class ComparableTripleTest {

    @Test
    public void comparableTripleEquality() {
        final ComparableTriple<Integer, Integer, Integer> ct1 = new ComparableTriple<>(1, 2, 3),
                ct2 = new ComparableTriple<>(1, 2, 3);
        Assert.assertEquals(0, ct1.compareTo(ct2));
    }

    @Test
    public void comparableTripleGetters() {
        final int first = 103, second = 1284, third = 192;
        final ComparableTriple<Integer, Integer, Integer> ct = new ComparableTriple<>(first, second, third);
        Assert.assertEquals(first, ct.getFirst().intValue());
        Assert.assertEquals(second, ct.getSecond().intValue());
        Assert.assertEquals(third, ct.getThird().intValue());
    }

    @Test
    public void comparableTripleSetters() {
        final int first = 103, second = 1285, third = 192,
                first_ = 104, second_ = 1284, third_ = 191;
        final ComparableTriple<Integer, Integer, Integer> ct = new ComparableTriple<>(first, second, third);

        ct.setFirst(first_);
        Assert.assertEquals(first_, ct.getFirst().intValue());

        ct.setSecond(second_);
        Assert.assertEquals(second_, ct.getSecond().intValue());

        ct.setThird(third_);
        Assert.assertEquals(third_, ct.getThird().intValue());

        ct.set(first, second, third);
        Assert.assertEquals(first, ct.getFirst().intValue());
        Assert.assertEquals(second, ct.getSecond().intValue());
        Assert.assertEquals(third, ct.getThird().intValue());
    }

    @Test
    public void comparableTripleComparisons() {
        final ComparableTriple<Double, Double, Double> ct1 = new ComparableTriple<>(1.0, 1.0, 1.0),
                ct2 = new ComparableTriple<>(1.0, 2.0, 1.0),
                ct3 = new ComparableTriple<>(1.0, 1.0, 2.0),
                ct4 = new ComparableTriple<>(2.0, 1.0, 1.0);

        Assert.assertEquals(-1, ct1.compareTo(ct2));
        Assert.assertEquals(-1, ct1.compareTo(ct3));
        Assert.assertEquals(-1, ct1.compareTo(ct4));

        Assert.assertEquals(1, ct4.compareTo(ct3));
        Assert.assertEquals(1, ct4.compareTo(ct2));
        Assert.assertEquals(1, ct4.compareTo(ct1));
    }

}
