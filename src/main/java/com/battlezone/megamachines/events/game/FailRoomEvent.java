package com.battlezone.megamachines.events.game;

public class FailRoomEvent {

    private final byte[] data;

    public FailRoomEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
