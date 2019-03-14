package com.battlezone.megamachines.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void padTest() {
        final String nonPadded = "hello",
                expected = "-----hello",
                actual = StringUtil.pad(nonPadded, 10, '-');
        Assert.assertEquals(expected, actual);
    }

}
