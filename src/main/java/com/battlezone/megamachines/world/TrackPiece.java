package com.battlezone.megamachines.world;

import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Shader;

public class TrackPiece extends GameObject {

    private TrackType type;

    TrackPiece(double x, double y, float scale, TrackType type) {
        super(x, y, scale);
        this.type = type;
    }

    public TrackType getType() {
        return type;
    }
}
