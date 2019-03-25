package com.battlezone.megamachines.util;

public class StringUtil {

    /**
     * Adds padding to the start of a string to make it a given length
     *
     * @param string  The string to pad
     * @param length  The length to pad it to
     * @param padding The character to pad it with
     * @return The padded string
     */
    public static String pad(String string, int length, char padding) {
        String result = string;
        while(result.length() < length) {
            result = padding + result;
        }
        return result;
    }

}
