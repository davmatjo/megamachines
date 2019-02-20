package com.battlezone.megamachines.util;

import com.battlezone.megamachines.math.MathUtils;

import java.util.Arrays;

public class ArrayUtil {

    public static <T> T safeGet(T[] array, int index) {
        if (index >= 0 && index < array.length) {
            return array[index];
        }
        return null;
    }

    public static <T> T safeGet(T[][] array, int index1, int index2) {
        if (index1 >= 0 && index1 < array.length) {
            return safeGet(array[index1], index2);
        }
        return null;
    }


    public static Boolean safeGet(boolean[][] array, int index1, int index2) {
        if (index1 >= 0 && index1 < array.length) {
            var array2 = array[index1];
            if (index2 >= 0 && index2 < array2.length)
                return array[index1][index2];
        }
        return null;
    }

    public static <T> void prettyPrint(T[][] array) {
        var str = Arrays.deepToString(array);
        str = str.replace("], ", "]\n");
        str = str.replace("[[", "[\n[");
        str = str.replace("]]", "]\n]");
        System.out.println(str);
    }

    public static <T> T randomElement(T[] array) {
        var index = MathUtils.randomInteger(0, array.length);
        return array[index];
    }

}
