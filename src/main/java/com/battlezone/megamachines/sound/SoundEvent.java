package com.battlezone.megamachines.sound;

public class SoundEvent {

    private String fileName;
    private int playTimeSeconds;
    private float volume;

    public static final int PLAY_FOREVER = -1;
    public static final int PLAY_ONCE = -2;

    public SoundEvent(String fileName, int playTimeSeconds, float volume) {
        this.fileName = fileName;
        this.playTimeSeconds = playTimeSeconds;
        this.volume = volume;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPlayTimeSeconds() {
        return playTimeSeconds;
    }

    public float getVolume() {
        return volume;
    }

}
