package com.battlezone.megamachines.math;

import org.junit.Assert;
import org.junit.Test;

public class MathTest {

    @Test
    public void pythag() {
        var distance = MathUtils.pythagoras(10, 10, 13, 14);
        Assert.assertEquals(distance, 5, 0.01);
    }

}
