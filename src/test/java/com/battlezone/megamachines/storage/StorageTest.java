package com.battlezone.megamachines.storage;

import com.battlezone.megamachines.math.Vector3f;
import org.junit.Assert;
import org.junit.Test;

public class StorageTest {

    @Test
    public void testRwInt() {
        var sp = Storage.getStorage();
        sp.setValue("test", 45);
        sp.setValue("test2", 23);
        Assert.assertEquals(sp.getInt("test", 0), 45);
        Assert.assertEquals(sp.getInt("test2", 0), 23);
    }

    @Test
    public void testDefaultInt() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getInt("test", 91), 91);
    }

    @Test
    public void testRwLong() {
        var sp = Storage.getStorage();
        sp.setValue("test", 50000000000L);
        sp.setValue("test2", 60000000000L);
        Assert.assertEquals(sp.getLong("test", 0), 50000000000L);
        Assert.assertEquals(sp.getLong("test2", 0), 60000000000L);
    }

    @Test
    public void testDefaultLong() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getLong("test", 10000000000L), 10000000000L);
    }

    @Test
    public void testRwVector3f() {
        var sp = Storage.getStorage();
        sp.setValue("test", new Vector3f(1f, 2f, 3f));
        sp.setValue("test2", new Vector3f(4f, 5f, 6f));
        Assert.assertEquals(sp.getVector3f("test", new Vector3f(0, 0, 0)), new Vector3f(1f, 2f, 3f));
        Assert.assertEquals(sp.getVector3f("test2", new Vector3f(0, 0, 0)), new Vector3f(4f, 5f, 6f));
    }

    @Test
    public void testDefaultVector3f() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getLong("test", 10000000000L), 10000000000L);
    }

    @Test
    public void testRWString() {
        var sp = Storage.getStorage();
        sp.setValue("test", "hello");
        sp.setValue("test2", "world");
        Assert.assertEquals(sp.getString("test", ""), "hello");
        Assert.assertEquals(sp.getString("test2", ""), "world");
    }

    @Test
    public void testDefaultString() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getString("test", "testing"), "testing");
    }

    @Test
    public void testRwFloat() {
        var sp = Storage.getStorage();
        sp.setValue("test", 10.6f);
        sp.setValue("test2", 20.1f);
        Assert.assertEquals(sp.getFloat("test", 0), 10.6f, 0);
        Assert.assertEquals(sp.getFloat("test2", 0), 20.1f, 0);
    }

    @Test
    public void testDefaultFloat() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getFloat("test", 23.3f), 23.3f, 0);
    }

    @Test
    public void testRwDouble() {
        var sp = Storage.getStorage();
        sp.setValue("test", 10.6);
        sp.setValue("test2", 20.1);
        Assert.assertEquals(sp.getDouble("test", 0), 10.6, 0);
        Assert.assertEquals(sp.getDouble("test2", 0), 20.1, 0);
    }

    @Test
    public void testDefaultDouble() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getDouble("test", 23.3), 23.3, 0);
    }

    @Test
    public void testRwBoolean() {
        var sp = Storage.getStorage();
        sp.setValue("test", true);
        sp.setValue("test2", false);
        Assert.assertTrue(sp.getBoolean("test", false));
        Assert.assertFalse(sp.getBoolean("test2", true));
    }

    @Test
    public void testDefaultBoolean() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertTrue(sp.getBoolean("test", true));
        Assert.assertFalse(sp.getBoolean("test2", false));
    }

    @Test
    public void testSave() {
        var sp = Storage.getStorage();
        sp.setValue("test", 27);
        sp.save();
        sp.setValue("test", 39);
        //Changes after save should be lost, but changes before the save retained
        sp.reload();
        Assert.assertEquals(sp.getInt("test", 0), 27);
    }

}
