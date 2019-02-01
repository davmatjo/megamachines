package com.battlezone.megamachines.storage;

import org.junit.Assert;
import org.junit.Test;

public class StorageTest {

    @Test
    public void testRW() {
        var sp = Storage.getStorage();
        sp.setValue(Storage.KEY_SFX_VOLUME, 45);
        Assert.assertEquals(sp.getInt(Storage.KEY_SFX_VOLUME, 0), 45);
    }

    @Test
    public void testDef() {
        var sp = Storage.getStorage();
        sp.clearAll();
        Assert.assertEquals(sp.getInt(Storage.KEY_SFX_VOLUME, 91), 91);
    }

    @Test
    public void testSave() {
        var sp = Storage.getStorage();
        sp.setValue(Storage.KEY_SFX_VOLUME, 27);
        sp.save();
        sp.setValue(Storage.KEY_SFX_VOLUME, 39);
        sp.reload();
        Assert.assertEquals(sp.getInt(Storage.KEY_SFX_VOLUME, 0), 27);
    }

}
