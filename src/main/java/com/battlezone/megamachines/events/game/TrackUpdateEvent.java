package com.battlezone.megamachines.events.game;

public class TrackUpdateEvent {

    private byte[] data;
    private byte[] bytes;

    /**
     * Create a TrackUpdateEvent
     * @param data the new track
     * @param bytes
     */
    public TrackUpdateEvent(byte[] data, byte[] bytes) {
        this.data = data;
        this.bytes = bytes;
    }

    public byte[] getTrackData() {
        return data;
    }

    public byte[] getManagerData() {
        return bytes;
    }
}
