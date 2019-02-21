package com.battlezone.megamachines.storage;

public class Storage {

    private final static StorageProvider storage = new JSONStorageProvider();

    public static StorageProvider getStorage() {
        return storage;
    }

    // key values
    public static final String BACKGROUND_MUSIC_VOLUME = "background_music_volume";
    public static final String SFX_VOLUME = "sfx_volume";
    public static final String CAR_MODEL = "car_model";
    public static final String CAR_COLOUR = "car_colour";
    public static final String IP_ADDRESS = "ip_addr";
    public static final String ROOM_NUMBER = "0";

}
