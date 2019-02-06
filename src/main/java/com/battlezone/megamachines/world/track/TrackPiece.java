package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.GameObject;

public class TrackPiece extends GameObject {

    private TrackType type;

    public TrackPiece(double x, double y, float scale, TrackType type) {
        super(x, y, scale);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }
}
