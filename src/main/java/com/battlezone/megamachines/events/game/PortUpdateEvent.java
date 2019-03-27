package com.battlezone.megamachines.events.game;

public class PortUpdateEvent {

    private final byte[] data;

    /**
     * Updates the port to be used by the UDP sockets
     *
     * @param data Data of the update
     */
    public PortUpdateEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
