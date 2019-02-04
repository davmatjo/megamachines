package com.battlezone.megamachines.util;

import java.util.*;

/**
 * A map with keys sorted by the values.
 *
 * @param <K> the class type for the Key.
 * @param <V> the class type for the Values.
 * @author Kieran
 */
public class ValueSortedMap<K, V extends Comparable> {

    private HashMap<K, V> internalMap = new HashMap<>();
    private List<K> internalList = new ArrayList<>();

    /**
     * @see HashMap#put(Object, Object)
     */
    public V put(K key, V value) {
        internalMap.put(key, value);
        internalList.add(key);
        internalList.sort(Comparator.comparing(this::get));
        return value;
    }

    /**
     * @see HashMap#get(Object)
     */
    public V get(K key) {
        return internalMap.get(key);
    }

    /**
     * @see HashMap#getOrDefault(Object, Object)
     */
    public V getOrDefault(K key, V defaultValue) {
        return internalMap.getOrDefault(key, defaultValue);
    }

    /**
     * @see HashMap#remove(Object)
     */
    public V remove(K key) {
        internalList.remove(key);
        return internalMap.remove(key);
    }

    /**
     * @see HashMap#clear()
     */
    public void clear() {
        internalList.clear();
        internalMap.clear();
    }

    /**
     * @see HashMap#keySet()
     */
    public Set<K> keySet() {
        return Set.copyOf(internalList);
    }

    /**
     * Method to return the keys as a list.
     *
     * @return the keys as a list.
     */
    public List<K> keyList() {
        return internalList;
    }

    /**
     * @see HashMap#containsKey(Object)
     */
    public boolean containsKey(K key) {
        return internalMap.containsKey(key);
    }

}
