package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.GameObject;

public class TrackPiece extends GameObject {

    private TrackType type;
    public static final float TRACK_SIZE = 10;


    public TrackPiece(double x, double y, TrackType type) {
        super(x, y, TRACK_SIZE);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }
}
