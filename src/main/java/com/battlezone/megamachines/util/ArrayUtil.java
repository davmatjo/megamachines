package com.battlezone.megamachines.util;

import com.battlezone.megamachines.math.MathUtils;

import java.util.Arrays;

public class ArrayUtil {

    /**
     * A method to safely get an item from an array at a given index.
     *
     * @param array The array to access.
     * @param index The index to read from.
     * @param <T>   The type of the array.
     * @return The item if it is within the array, null if the index is out of range.
     */
    public static <T> T safeGet(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index];
        }
        return null;
    }

    /**
     * A method to safely get an item from a 2D array at a given index.
     *
     * @param array  The 2D array to access.
     * @param index1 The first index to read from.
     * @param index2 The second index to read from.
     * @param <T>    The type of the 2D array.
     * @return The item if it is within the array, null if the index is out of range.
     */
    public static <T> T safeGet(T[][] array, int index1, int index2) {
        if (index1 >= 0 && index1 < array.length) {
            return safeGet(array[index1], index2);
        }
        return null;
    }

    /**
     * A method to safely get a boolean from a 2D boolean array at a given index.
     *
     * @param array  The 2D boolean array to access.
     * @param index1 The first index to read from.
     * @param index2 The second index to read from.
     * @return The item if it is within the array, null if the index is out of range.
     */
    public static Boolean safeGet(boolean[][] array, int index1, int index2) {
        if (index1 >= 0 && index1 < array.length) {
            var array2 = array[index1];
            if (index2 >= 0 && index2 < array2.length)
                return array[index1][index2];
        }
        return null;
    }

    /**
     * A method to print a 2D array to the console.
     *
     * @param array The array to print.
     * @param <T>   The type of the array.
     */
    public static <T> void prettyPrint(T[][] array) {
        var str = Arrays.deepToString(array);
        str = str.replace("], ", "]\n");
        str = str.replace("[[", "[\n[");
        str = str.replace("]]", "]\n]");
        System.out.println(str);
    }

    /**
     * A method to choose a random element from an array.
     *
     * @param array The array to choose from.
     * @param <T>   The type of the array to choose from.
     * @return A random element from the array.
     */
    public static <T> T randomElement(T[] array) {
        var index = MathUtils.randomInteger(0, array.length);
        return array[index];
    }

}
