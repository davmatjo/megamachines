package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

import java.util.List;

/**
 * This class contains all methods related to collissions between entities in our game world
 */
public abstract class Collisions {

    /**
     * Returns the triangle area of the 3 points
     * The value is positive if there is a left turn on the second point
     * 0 if the points form a line
     * negative if the is a right turn on the second turn
     * @param firstPoint The x,y position of the first point
     * @param secondPoint The x,y position of the second point
     * @param thirdPoint The x,y position of the third point
     * @return The area of the triangle
     */
    private static double triangleArea(Pair<Double, Double> firstPoint, Pair<Double, Double> secondPoint, Pair<Double, Double> thirdPoint) {
        //Math magic. Ask Stefan about this if interested.
        return firstPoint.getFirst() * (secondPoint.getSecond() - thirdPoint.getSecond()) -
                firstPoint.getSecond() * (secondPoint.getFirst() - thirdPoint.getFirst()) +
                secondPoint.getFirst() * thirdPoint.getSecond() - secondPoint.getSecond() * thirdPoint.getFirst();
    }

    /**
     * Returns true if the rectangle contains the specified point, false otherwise
     * A point that's situated on the edge counts as a collision
     * @param rectangle The rectangle
     * @param point The point
     * @return True if the point is contained by the rectangle, false otherwise
     */
    private static boolean contains(List<Pair<Double,Double>> rectangle, Pair<Double,Double> point) {
        for (int i = 0; i < 4; i++) {
            //Left turn means that point is outside of rectangle
            if (triangleArea(rectangle.get(i), rectangle.get((i + 1) % 4), point) > 0) {
                return false;
            }
            //A straight line means that we have to check whether the point is on the edge
            else if (triangleArea(rectangle.get(i), rectangle.get((i + 1) % 4), point) == 0) {
                if (point.getFirst() <= rectangle.get(i).getFirst() && point.getFirst() <= rectangle.get((i + 1) % 4).getFirst()) {
                    return false;
                } else if (point.getFirst() >= rectangle.get(i).getFirst() && point.getFirst() >= rectangle.get((i + 1) % 4).getFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        //Right turn every time means that point has to be inside of rectangle
        return true;
    }

    /**
     * Detects whether the hitboxes of 2 objects (represented by rectangles) have collided.
     * WARNING: Please input the (x,y) position of each vertex starting from the top left one, and then moving clockwise
     * We understand top left as the front left headlight of a car
     * @param firstRectangle The points of the first rectangle. Please read warning
     * @param secondRectangle The points of the second rectangle. Please read warning
     * @return True if the rectangles collided, false otherwise
     */
    public static boolean collided(List<Pair<Double, Double>> firstRectangle, List<Pair<Double, Double>> secondRectangle) {
        assert(firstRectangle.size() == 4 && secondRectangle.size() == 4);

        for (int i = 0; i < 4; i++) {
            if (contains(firstRectangle, secondRectangle.get(i)) || contains(secondRectangle, firstRectangle.get(i))) {
                return true;
            }
        }
        return false;
    }
}
