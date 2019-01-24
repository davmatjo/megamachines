package com.battlezone.megamachines.util;

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

    public static <T> void prettyPrint(T[][] array) {
        var str = Arrays.deepToString(array);
        str = str.replace("], ", "]\n");
        str = str.replace("[[", "[\n[");
        str = str.replace("]]", "]\n]");
        System.out.println(str);
    }

}
