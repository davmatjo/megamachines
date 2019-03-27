package com.battlezone.megamachines.events.game;

public class FailRoomEvent {

    private final byte[] data;

    /**
     * Creates a FailRoomEvent which occurs when creation of a multiplayer room has failed
     *
     * @param data Data of this failure
     */
    public FailRoomEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
