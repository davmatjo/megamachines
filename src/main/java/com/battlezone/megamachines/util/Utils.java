package com.battlezone.megamachines.util;

public class Utils {

    /**
     * Method to determine whether an element is equal to at least one of a given list of elements.
     *
     * @param element
     * @param elements
     * @return
     */
    public static boolean equalsOr(Object element, Object... elements) {
        for (int i = 0; i < elements.length; i++) {
            final Object o = elements[i];
            if ((element == null || o == null) && element != o) return false;
            else if (!element.equals(o)) return false;
        }
        return true;
    }

}
