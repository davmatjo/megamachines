package com.battlezone.megamachines.world;

public enum TrackType {
    UP, DOWN, LEFT, RIGHT,
    UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
    LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN,
    UNKNOWN;

    /**
     * Get the file name for the asset which represents this type of track
     *
     * @return The filename for the asset
     */
    public String getFileName() {
        String prefix = "tracks/track_";
        switch (this) {
            case UP:
                return prefix + "u.png";
            case DOWN:
                return prefix + "d.png";
            case LEFT:
                return prefix + "l.png";
            case RIGHT:
                return prefix + "r.png";
            case UP_RIGHT:
                return prefix + "u_r.png";
            case UP_LEFT:
                return prefix + "u_l.png";
            case DOWN_RIGHT:
                return prefix + "d_r.png";
            case DOWN_LEFT:
                return prefix + "d_l.png";
            case LEFT_UP:
                return prefix + "l_u.png";
            case LEFT_DOWN:
                return prefix + "l_d.png";
            case RIGHT_UP:
                return prefix + "r_u.png";
            case RIGHT_DOWN:
                return prefix + "r_d.png";
        }
        return "";
    }
}
