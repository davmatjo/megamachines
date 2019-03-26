package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.GameObject;
import com.battlezone.megamachines.world.ScaleController;

public class TrackPiece extends GameObject {

    private TrackType type;

    public TrackPiece(double x, double y, TrackType type) {
        super(x, y, ScaleController.TRACK_SCALE);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }
}
