package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.world.track.Track;

public class TrackUpdateEvent {

    private byte[] data;

    public TrackUpdateEvent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
