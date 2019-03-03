package com.battlezone.megamachines.events.game;

public class PowerupTriggerEvent {

    private final byte[] data;

    public PowerupTriggerEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
