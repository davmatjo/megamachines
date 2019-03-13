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
     *
     * @param firstPoint  The x,y position of the first point
     * @param secondPoint The x,y position of the second point
     * @param thirdPoint  The x,y position of the third point
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
     *
     * @param rectangle The rectangle
     * @param point     The point
     * @return True if the point is contained by the rectangle, false otherwise
     */
    private static boolean contains(List<Pair<Double, Double>> rectangle, Pair<Double, Double> point) {
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
     * Used to compute the normal vector of the collision
     *
     * @param rectangle         The rectangle of the first object involved in the collision
     * @param p                 The point where the objects collide
     * @param firstBodyRotation The rotation of the first object
     * @return The normal vector of the collision
     */
    public static Pair<Double, Double> getN(List<Pair<Double, Double>> rectangle, Pair<Double, Double> p, double firstBodyRotation) {
        double min = Double.MAX_VALUE;
        int which = 0;
        for (int i = 0; i < 4; i++) {
            double x1 = rectangle.get(i % 4).getFirst();
            double y1 = rectangle.get(i % 4).getSecond();
            double x2 = rectangle.get((i + 1) % 4).getFirst();
            double y2 = rectangle.get((i + 1) % 4).getSecond();
            double x0 = p.getFirst();
            double y0 = p.getSecond();

            double dist = Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / (Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2)));
            if (dist < min) {
                min = dist;
                which = i;
            }
        }

        if (which == 0) {
            return new Pair<>(1.0, firstBodyRotation);
        } else if (which == 1) {
            return new Pair<>(1.0, firstBodyRotation - 90);
        } else if (which == 2) {
            return new Pair<>(1.0, firstBodyRotation - 180);
        } else {
            return new Pair<>(1.0, firstBodyRotation + 90);
        }

    }

    /**
     * Detects whether the hitboxes of 2 objects (represented by rectangles) have collided.
     * WARNING: Please input the (x,y) position of each vertex starting from the top left one, and then moving clockwise
     * We understand top left as the front left headlight of a car
     *
     * @param firstRectangle  The points of the first rectangle. Please read warning
     * @param secondRectangle The points of the second rectangle. Please read warning
     * @return True if the rectangles collided, false otherwise
     */
    public static Pair<Pair<Double, Double>, Pair<Double, Double>> hitboxesCollided(List<Pair<Double, Double>> firstRectangle, List<Pair<Double, Double>> secondRectangle, double firstBodyRotation) {
        assert (firstRectangle.size() == 4 && secondRectangle.size() == 4);

        for (int i = 0; i < 4; i++) {
            if (contains(firstRectangle, secondRectangle.get(i))) {
                return new Pair<>(secondRectangle.get(i), getN(firstRectangle, secondRectangle.get(i), firstBodyRotation));
            }
        }
        return null;
    }

    /**
     * Returns true if the objects have collided, false otherwise
     *
     * @param firstObject  The first object to be checked
     * @param secondObject The second object to be checked
     * @return True if the objects have collided, false otherwise
     */
    public static Pair<Pair<Double, Double>, Pair<Double, Double>> objectsCollided(List<List<Pair<Double, Double>>> firstObject, List<List<Pair<Double, Double>>> secondObject, double firstBodyRotation) {
        for (int i = 0; i < firstObject.size(); i++) {
            for (int j = i; j < secondObject.size(); j++) {
                var haveCollided = hitboxesCollided(firstObject.get(i), secondObject.get(j), firstBodyRotation);
                if (haveCollided != null) {
                    return haveCollided;
                }
            }
        }
        return null;
    }

}
