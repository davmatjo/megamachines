package com.battlezone.megamachines.storage;

public class Storage {

    private final static JSONStorageProvider storage = new JSONStorageProvider();

    public static JSONStorageProvider getStorage() {
        return storage;
    }

    // key values
    public static final String KEY_BACKGROUND_MUSIC_VOLUME = "background_music_volume";
    public static final String KEY_SFX_VOLUME = "sfx_volume";

}
