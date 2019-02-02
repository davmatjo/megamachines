package com.battlezone.megamachines.storage;

public class Storage {

    private final static StorageProvider storage = new JSONStorageProvider();

    static StorageProvider getStorage() {
        return storage;
    }

    // key values
    public static final String KEY_BACKGROUND_MUSIC_VOLUME = "background_music_volume";
    public static final String KEY_SFX_VOLUME = "sfx_volume";

}
