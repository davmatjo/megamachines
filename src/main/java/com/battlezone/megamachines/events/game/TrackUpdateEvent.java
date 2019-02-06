package com.battlezone.megamachines.events.game;

import com.battlezone.megamachines.world.track.Track;

public class TrackUpdateEvent {

    private Track track;

    public TrackUpdateEvent(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }
}
