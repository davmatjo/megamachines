package com.battlezone.megamachines.util;

import org.junit.Assert;
import org.junit.Test;

public class ValueSortedMapTest {

    @Test
    public void keysRetainValueOrder() {
        ValueSortedMap<String, Double> vsm = new ValueSortedMap<>();
        vsm.put("Item 1", 1.5d);
        vsm.put("Item 2", 1.7d);
        vsm.put("Item 3", 3.6d);

        final String[] expected = new String[]{"Item 1", "Item 2", "Item 3"};
        Assert.assertArrayEquals(expected, vsm.keyList().toArray(new String[]{}));
    }

    @Test
    public void keysGainValueOrder() {
        ValueSortedMap<String, Double> vsm = new ValueSortedMap<>();
        vsm.put("Egg", 1.5d);
        vsm.put("Apple", 3.6d);
        vsm.put("Banana", 1.7d);

        final String[] expected = new String[]{"Egg", "Banana", "Apple"};
        Assert.assertArrayEquals(expected, vsm.keyList().toArray(new String[]{}));
    }

    @Test
    public void keysGainUpdatedValueOrder() {
        ValueSortedMap<String, Double> vsm = new ValueSortedMap<>();
        vsm.put("Item 1", 1.5d);
        vsm.put("Item 2", 1.7d);
        vsm.put("Item 3", 3.6d);

        // Update Item 3
        vsm.put("Item 3", 1.6d);

        final String[] expected = new String[]{"Item 1", "Item 3", "Item 2"};
        Assert.assertArrayEquals(expected, vsm.keyList().toArray(new String[]{}));
    }

    @Test
    public void containsKey() {
        ValueSortedMap<String, Integer> vsm = new ValueSortedMap<>();
        final String KEY = "Test";
        Assert.assertFalse(vsm.containsKey(KEY));
        Assert.assertFalse(vsm.keySet().contains(KEY));
        Assert.assertFalse(vsm.keyList().contains(KEY));

        vsm.put(KEY, 0);

        Assert.assertTrue(vsm.containsKey(KEY));
        Assert.assertTrue(vsm.keySet().contains(KEY));
        Assert.assertTrue(vsm.keyList().contains(KEY));
    }

    @Test
    public void deletingKey() {
        ValueSortedMap<String, Double> vsm = new ValueSortedMap<>();
        vsm.put("Apple", 0.67d);
        vsm.put("Banana", 0.7d);
        vsm.put("Grapes", 1.6d);
        vsm.put("Carrot", 0.2d);
        vsm.put("Onion", 0.3d);

        final String DELETION = "Grapes";

        vsm.remove(DELETION);

        Assert.assertFalse(vsm.containsKey(DELETION));
        Assert.assertFalse(vsm.keySet().contains(DELETION));
        Assert.assertFalse(vsm.keyList().contains(DELETION));

    }

    @Test
    public void clearingMap() {
        ValueSortedMap<String, Integer> vsm = new ValueSortedMap<>();
        final String[] keys = new String[]{"Item 1", "Item 2", "Item 3", "Item 4"};

        for (String key : keys)
            Assert.assertFalse(vsm.containsKey(key));

        for (String key : keys)
            vsm.put(key, 0);

        for (String key : keys)
            Assert.assertTrue(vsm.containsKey(key));

        vsm.clear();

        for (String key : keys)
            Assert.assertFalse(vsm.containsKey(key));

        Assert.assertEquals(0, vsm.keySet().size());
        Assert.assertEquals(0, vsm.keyList().size());
    }

}
