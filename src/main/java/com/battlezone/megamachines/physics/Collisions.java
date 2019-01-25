package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

import java.util.List;

/**
 * This class contains all methods related to collissions between entities in our game world
 */
public abstract class Collisions {

    /**
     * Returns true if the path from the first point, passing through the second point to reach the third point makes a left turn
     * @param firstPoint The x,y position of the first point
     * @param second The x,y position of the second point
     * @param thirdPoint The x,y position of the third point
     * @return True if left turn, false otherwise
     */
    private boolean leftTurn(Pair<Double, Double> firstPoint, Pair<Double, Double> second, Pair<Double, Double> thirdPoint,)


    /**
     * Detects whether the hitboxes of 2 objects (represented by rectangles) have collided.
     * WARNING: Please input the (x,y) position of each vertex starting from the top left one, and then moving clockwise
     * We understand top left as the front left headlight of a car
     * @param firstRectangle The points of the first rectangle. Please read warning
     * @param secondRectangle The points of the second rectangle. Please read warning
     * @return True if the rectangles collided, false otherwise
     */
    public static boolean collided(List<Pair<Double, Double>> firstRectangle, List<Paid<Double, Double>> secondRectangle) {
        assert(firstRectangle.size() == 4 && secondRectangle.size() == 4);

        //TODO: Finish this
        return false;
    }

}
