package com.battlezone.megamachines.events.game;

public class PortUpdateEvent {

    private final byte[] data;

    public PortUpdateEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
