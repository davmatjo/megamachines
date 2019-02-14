package com.battlezone.megamachines.util;

import java.util.*;

/**
 * A map with keys sorted by the values.
 *
 * @param <K> the class type for the Key.
 * @param <V> the class type for the Values.
 * @author Kieran
 */
public class ValueSortedMap<K, V extends Comparable> extends HashMap<K, V> {

    private SortedSet<K> temporarySet = new TreeSet<K>(Comparator.comparing(super::get, (v1, v2) -> {
        // Catch nulls
        if (v1 == null && v2 == null) {
            // Equal
            return 0;
        } else if (v1 == null) {
            // Less than other argument
            return -1;
        } else if (v2 == null) {
            // Greater than other argument
            return 1;
        } else {
            // Normal case
            return v1.compareTo(v2);
        }
    }));
    private List<K> temporaryList = new ArrayList<>(temporarySet);

    public <K, V> ValueSortedMap() {
        super();
    }

    public <K, V> ValueSortedMap(Comparator comparator) {
        super();
        temporarySet = new TreeSet<>(comparator);
    }

    /**
     * @see HashMap#keySet()
     */
    @Override
    public Set<K> keySet() {
        temporarySet.clear();
        temporarySet.addAll(super.keySet());
        return temporarySet;
    }

    /**
     * Method to return the keys as a list.
     *
     * @return the keys as a list.
     */
    public List<K> keyList() {
        temporaryList.clear();
        temporaryList.addAll(keySet());
        return temporaryList;
    }

}
