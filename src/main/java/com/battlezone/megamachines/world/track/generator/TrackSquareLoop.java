package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.world.track.TrackType;

public class TrackSquareLoop extends TrackGenerator {

    private final boolean CLOCKWISE;

    /**
     * Creates a basic square track from the given dimensions, as well as making it clockwise or anticlockwise.
     *
     * @param tracksAcross The number of tracks across.
     * @param tracksDown   The number of tracks down.
     * @param clockwise    True if the track is clockwise, flase if anticlockwise.
     */
    public TrackSquareLoop(int tracksAcross, int tracksDown, boolean clockwise) {
        super(tracksAcross, tracksDown);
        CLOCKWISE = clockwise;
    }

    /**
     * The method to generate the map.
     */
    @Override
    void generateMap() {
        final int X_MAX = tracksAcross - 1, Y_MAX = tracksDown - 1;
        // Horizontals
        for (int i = 1; i < X_MAX; i++) {
            grid[i][0] = CLOCKWISE ? TrackType.LEFT : TrackType.RIGHT;
            grid[i][Y_MAX] = CLOCKWISE ? TrackType.RIGHT : TrackType.LEFT;
        }
        // Verticals
        for (int i = 1; i < Y_MAX; i++) {
            grid[0][i] = CLOCKWISE ? TrackType.UP : TrackType.DOWN;
            grid[X_MAX][i] = CLOCKWISE ? TrackType.DOWN : TrackType.UP;
        }
        // Top left corner
        grid[0][Y_MAX] = CLOCKWISE ? TrackType.UP_RIGHT : TrackType.LEFT_DOWN;
        // Top right corner
        grid[X_MAX][Y_MAX] = CLOCKWISE ? TrackType.RIGHT_DOWN : TrackType.UP_LEFT;
        // Bottom right corner
        grid[X_MAX][0] = CLOCKWISE ? TrackType.DOWN_LEFT : TrackType.RIGHT_UP;
        // Botom left corner
        grid[0][0] = CLOCKWISE ? TrackType.LEFT_UP : TrackType.DOWN_RIGHT;
    }
}
