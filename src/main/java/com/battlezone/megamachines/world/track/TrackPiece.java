package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.GameObject;
import com.battlezone.megamachines.world.ScaleController;

public class TrackPiece extends GameObject {

    private TrackType type;

    /**
     * Creates a Track piece at a given world coordinate of a specified type.
     *
     * @param x    The X coordinate of the track piece in the world.
     * @param y    The Y coordinate of the track piece in the world.
     * @param type The type of the track piece.
     */
    public TrackPiece(double x, double y, TrackType type) {
        super(x, y, ScaleController.TRACK_SCALE);
        this.type = type;
    }

    /**
     * A method to get the type of the track piece.
     *
     * @return The type of the track piece.
     */
    public TrackType getType() {
        return type;
    }
}
