package com.battlezone.megamachines.world.track.generator;

public class TrackCircleLoop extends TrackGenerator {

    final double ratio, radiusX, radiusY;
    final double maxblocks_x, maxblocks_y;

    public TrackCircleLoop(int tracksAcross, int tracksDown) {
        super(tracksAcross, tracksDown);
        ratio = tracksAcross / tracksDown;
        radiusX = tracksAcross / 2d;
        radiusY = tracksDown / 2d;

        if ((radiusX * 2) % 2 == 0) {
            maxblocks_x = Math.ceil(radiusX - .5) * 2 + 1;
        } else {
            maxblocks_x = Math.ceil(radiusX) * 2;
        }

        if ((radiusY * 2) % 2 == 0) {
            maxblocks_y = Math.ceil(radiusY - .5) * 2 + 1;
        } else {
            maxblocks_y = Math.ceil(radiusY) * 2;
        }

    }

    @Override
    void generateMap() {
        for (double y = -maxblocks_y / 2 + 1; y <= maxblocks_y / 2 - 1; y++) {
            for (double x = -maxblocks_x / 2 + 1; x <= maxblocks_x / 2 - 1; x++) {
//                var xfilled;

//                grid[x][y] = innerfilled(x, y, radiusX) ? TrackType.UP : null;
//            }
//        }
            }
        }

        /**
         * var distance  = function( x, y, ratio ) {
         * return Math.sqrt((Math.pow(y * ratio, 2)) + Math.pow(x, 2));
         * },
         * filled = function( x, y, radius, ratio ) {
         * return distance(x, y, ratio) <= radius;
         * },
         * fatfilled = function( x, y, radius, ratio ) {
         * return filled(x, y, radius, ratio) && !(
         * filled(x + 1, y, radius, ratio) &&
         * filled(x - 1, y, radius, ratio) &&
         * filled(x, y + 1, radius, ratio) &&
         * filled(x, y - 1, radius, ratio) &&
         * filled(x + 1, y + 1, radius, ratio) &&
         * filled(x + 1, y - 1, radius, ratio) &&
         * filled(x - 1, y - 1, radius, ratio) &&
         * filled(x - 1, y + 1, radius, ratio)
         * );
         * };
         */
    }

    private double distance(int x, int y) {
        return Math.sqrt((Math.pow(y * ratio, 2)) + Math.pow(x, 2));
    }

    private boolean filled(int x, int y, double radius) {
        return distance(x, y) <= radius;
    }

    private boolean innerfilled(int x, int y, double radius) {
        return filled(x, y, radius) && !(
                filled(x + 1, y, radius) &&
                        filled(x - 1, y, radius) &&
                        filled(x, y + 1, radius) &&
                        filled(x, y - 1, radius) &&
                        filled(x + 1, y + 1, radius) &&
                        filled(x + 1, y - 1, radius) &&
                        filled(x - 1, y - 1, radius) &&
                        filled(x - 1, y + 1, radius)
        );
    }
}
