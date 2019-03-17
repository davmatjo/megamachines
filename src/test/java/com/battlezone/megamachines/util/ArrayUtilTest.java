package com.battlezone.megamachines.util;

import com.battlezone.megamachines.math.MathUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;

import static com.battlezone.megamachines.util.ArrayUtil.safeGet;

public class ArrayUtilTest {

    @Test
    public void safeGet1D() {
        final Integer[] array = new Integer[]{
                1, 2, 3, 4
        };
        // Valid
        for (int i = 0; i < array.length; i++)
            Assert.assertEquals(array[i], safeGet(array, i));
        // Invalid
        Assert.assertEquals(null, safeGet(array, -1));
        Assert.assertEquals(null, safeGet(array, 100));
    }

    @Test
    public void safeGet2D() {
        final Integer[][] array = new Integer[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8}
        };
        // Valid
        for (int x = 0; x < array.length; x++)
            for (int y = 0; y < array[x].length; y++)
                Assert.assertEquals(array[x][y], safeGet(array, x, y));
        // Invalid
        Assert.assertEquals(null, safeGet(array, -1, -1));
        Assert.assertEquals(null, safeGet(array, 0, -1));
        Assert.assertEquals(null, safeGet(array, 100, 0));
        Assert.assertEquals(null, safeGet(array, 1298310, 0));
    }

    @Test
    public void safeGetBool() {
        final boolean[][] array = new boolean[][]{
                {true, true},
                {false, true}
        };
        // Valid
        for (int x = 0; x < array.length; x++)
            for (int y = 0; y < array[x].length; y++)
                Assert.assertEquals(array[x][y], safeGet(array, x, y));
        // Invalid
        Assert.assertEquals(null, safeGet(array, -1, -1));
        Assert.assertEquals(null, safeGet(array, 0, -1));
        Assert.assertEquals(null, safeGet(array, 100, 0));
        Assert.assertEquals(null, safeGet(array, 1298310, 0));
    }

    @Test
    public void prettyPrint() {
        final Integer[][] array = new Integer[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8}
        };
        final String expected = "[\n[1, 2, 3, 4]\n[5, 6, 7, 8]\n]\n";

        // Make an output stream that writes to a string to check for System.out.println
        OutputStream output = new OutputStream() {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) {
                this.string.append((char) b);
            }

            public String toString() {
                return this.string.toString();
            }
        };

        System.setOut(new PrintStream(output));
        ArrayUtil.prettyPrint(array);
//        Assert.assertEquals(expected, output.toString());
    }

    @Test
    public void randomElement() {
        final Integer[] array = new Integer[]{
                1, 2, 3, 4
        };
        for (int i = 0; i < 100000; i++) {
            final int r = ArrayUtil.randomElement(array);
            Assert.assertTrue(MathUtils.inRange(r, 1, 4));
        }
    }

}
