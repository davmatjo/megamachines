package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.world.track.TrackType;

public class TrackCircleLoop extends TrackGenerator {

    private final int width, height;
    private final double width_r, height_r, ratio, maxblocks_x, maxblocks_y;
    private final boolean CLOCKWISE;
    private boolean[][] circleGrid;

    /**
     * A track generator that creates a track in the shape of a circle/oval based on a width and height. The track will
     * be either clockwise or anticlockwise as specified.
     *
     * @param tracksAcross The width of the circle/oval.
     * @param tracksDown   The height of the circle/oval.
     * @param clockwise    True if the track should be clockwise, false for anticlockwise.
     */
    public TrackCircleLoop(int tracksAcross, int tracksDown, boolean clockwise) {
        super(tracksAcross, tracksDown);
        width = tracksAcross;
        height = tracksDown;
        circleGrid = new boolean[width][height];
        width_r = width / 2d;
        height_r = height / 2d;
        ratio = width_r / height_r;
        CLOCKWISE = clockwise;

        if ((width_r * 2) % 2 == 0)
            maxblocks_x = Math.ceil(width_r - .5) * 2 + 1;
        else
            maxblocks_x = Math.ceil(width_r) * 2;

        if ((height_r * 2) % 2 == 0)
            maxblocks_y = Math.ceil(height_r - .5) * 2 + 1;
        else
            maxblocks_y = Math.ceil(height_r) * 2;

        int i = 0;
        for (double y = -maxblocks_y / 2d + 1d; y <= maxblocks_y / 2d - 1d; y++) {
            int j = 0;
            for (double x = -maxblocks_x / 2d + 1d; x <= maxblocks_x / 2d - 1d; x++) {
                circleGrid[j][i] = fatfilled(x, y);
                j++;
            }
            i++;
        }
    }

    /**
     * A method to determine the distance of a point from (0, 0).
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @return The distance from the point to (0, 0).
     */
    private double distance(double x, double y) {
        return Math.sqrt((Math.pow(y * ratio, 2)) + Math.pow(x, 2));
    }

    /**
     * Determines whether a point is within the circle or not.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @return Whether the point is within the circle or not.
     */
    private boolean filled(double x, double y) {
        return distance(x, y) <= width_r;
    }

    /**
     * Determines whether a point should be filled based on the outlining of the circle.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     * @return Whether the point should be filled.
     */
    private boolean fatfilled(double x, double y) {
        return filled(x, y) && !(
                filled(x + 1, y) &&
                        filled(x - 1, y) &&
                        filled(x, y + 1) &&
                        filled(x, y - 1) &&
                        filled(x + 1, y + 1) &&
                        filled(x + 1, y - 1) &&
                        filled(x - 1, y - 1) &&
                        filled(x - 1, y + 1)
        );
    }

    /**
     * A method to generate the map from the circle.
     */
    @Override
    void generateMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (circleGrid[x][y])
                    grid[x][y] = getType(x, y);
            }
        }
    }

    /**
     * A method to determine the track piece type for a given X and Y coordinate.
     *
     * @param x The X coordinate to get the type of.
     * @param y The Y coordinate to get the type of.
     * @return The type of track it should be.
     */
    private TrackType getType(int x, int y) {
        final boolean
                lower_half = y < height_r,
                closer_half = x < width_r,
                track_up = (y < height - 1) && circleGrid[x][y + 1],
                track_down = (y > 0) && circleGrid[x][y - 1],
                track_left = (x > 0) && circleGrid[x - 1][y],
                track_right = (x < width - 1) && circleGrid[x + 1][y];

        if (CLOCKWISE) {
            if (track_up && track_down)
                // Verticals
                return closer_half ? TrackType.UP : TrackType.DOWN;
            else if (track_left && track_right)
                // Horizontals
                return lower_half ? TrackType.LEFT : TrackType.RIGHT;
            else if (track_up && track_right)
                // |_ angles
                return lower_half ? TrackType.LEFT_UP : TrackType.DOWN_RIGHT;
            else if (track_up && track_left)
                // _| angles
                return lower_half ? TrackType.DOWN_LEFT : TrackType.RIGHT_UP;
            else if (track_down && track_left)
                // ⌝ angles
                return lower_half ? TrackType.UP_LEFT : TrackType.RIGHT_DOWN;
            else
                // ⌜ angles
                return lower_half ? TrackType.LEFT_DOWN : TrackType.UP_RIGHT;
        } else {
            if (track_up && track_down)
                // Verticals
                return closer_half ? TrackType.DOWN : TrackType.UP;
            else if (track_left && track_right)
                // Horizontals
                return lower_half ? TrackType.RIGHT : TrackType.LEFT;
            else if (track_up && track_right)
                // |_ angles
                return lower_half ? TrackType.DOWN_RIGHT : TrackType.LEFT_UP;
            else if (track_up && track_left)
                // _| angles
                return lower_half ? TrackType.RIGHT_UP : TrackType.DOWN_LEFT;
            else if (track_down && track_left)
                // ⌝ angles
                return lower_half ? TrackType.RIGHT_DOWN : TrackType.UP_LEFT;
            else
                // ⌜ angles
                return lower_half ? TrackType.UP_RIGHT : TrackType.LEFT_DOWN;
        }

    }
}
