package com.battlezone.megamachines.events.game;

public class TrackUpdateEvent {

    private byte[] data;
    private byte[] bytes;
    private byte lapCounter;

    /**
     * Create a TrackUpdateEvent
     *
     * @param data  the new track
     * @param bytes
     */
    public TrackUpdateEvent(byte[] data, byte[] bytes, byte lapCounter) {
        this.data = data;
        this.bytes = bytes;
        this.lapCounter = lapCounter;
    }

    public byte[] getTrackData() {
        return data;
    }

    public byte[] getManagerData() {
        return bytes;
    }

    public byte getLapCounter() {
        return lapCounter;
    }
}
