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

    public byte toByte() {
        switch (this) {
            case UP:    return 0;
            case DOWN:  return 1;
            case LEFT:  return 2;
            case RIGHT: return 3;
            case LEFT_UP: return 4;
            case UP_LEFT: return 5;
            case RIGHT_UP: return 6;
            case UP_RIGHT: return 7;
            case DOWN_LEFT: return 8;
            case LEFT_DOWN: return 9;
            case DOWN_RIGHT: return 10;
            case RIGHT_DOWN: return 11;
            default: return -1;
        }
    }

    public static TrackType fromByte(byte b) {
        switch (b) {
            case 0: return UP;
            case 1: return DOWN;
            case 2: return LEFT;
            case 3: return RIGHT;
            case 4: return LEFT_UP;
            case 5: return UP_LEFT;
            case 6: return RIGHT_UP;
            case 7: return UP_RIGHT;
            case 8: return DOWN_LEFT;
            case 9: return LEFT_DOWN;
            case 10: return DOWN_RIGHT;
            case 11: return RIGHT_DOWN;
            default: return null;
        }
    }
}