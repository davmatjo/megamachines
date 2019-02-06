package com.battlezone.megamachines.world.track;

public enum TrackType {
    UP, DOWN, LEFT, RIGHT,
    UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
    LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN;

    public TrackType initialDirection() {
        switch (this) {
            case UP_RIGHT:
            case UP_LEFT:
                return UP;
            case DOWN_RIGHT:
            case DOWN_LEFT:
                return DOWN;
            case LEFT_UP:
            case LEFT_DOWN:
                return LEFT;
            case RIGHT_UP:
            case RIGHT_DOWN:
                return RIGHT;
            default:
                return this;
        }
    }

    public TrackType finalDirection() {
        switch (this) {
            case UP_RIGHT:
            case DOWN_RIGHT:
                return RIGHT;
            case UP_LEFT:
            case DOWN_LEFT:
                return LEFT;
            case LEFT_UP:
            case RIGHT_UP:
                return UP;
            case LEFT_DOWN:
            case RIGHT_DOWN:
                return DOWN;
            default:
                return this;
        }
    }

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
