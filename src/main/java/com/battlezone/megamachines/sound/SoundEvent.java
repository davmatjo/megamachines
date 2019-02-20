package com.battlezone.megamachines.sound;

import com.battlezone.megamachines.math.Vector2f;

public class SoundEvent {

    private String fileName;
    private int playTimeSeconds;
    private float volume;
    private Vector2f position;
    private Vector2f velocity;

    public static final int PLAY_FOREVER = -1;
    public static final int PLAY_ONCE = -2;

    public SoundEvent(String fileName, int playTimeSeconds, float volume) {
        this(fileName, playTimeSeconds, volume, new Vector2f(0, 0), new Vector2f(0, 0));
    }

    public SoundEvent(String fileName, int playTimeSeconds, float volume, Vector2f position, Vector2f velocity) {
        this.fileName = fileName;
        this.playTimeSeconds = playTimeSeconds;
        this.volume = volume;
        this.position = position;
        this.velocity = velocity;
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

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getVelocity() {
        return velocity;
    }

}
