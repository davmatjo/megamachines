package com.battlezone.megamachines.events.game;

public class TrackUpdateEvent {

    private byte[] data;

    /**
     * Create a TrackUpdateEvent
     * @param data the new track
     */
    public TrackUpdateEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
