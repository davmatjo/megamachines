package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class LineCollisions {
    private static Pair<Double, Double> linesIntersect(Pair<Pair<Double, Double>,Pair<Double, Double>> firstLine, Pair<Pair<Double, Double>,Pair<Double, Double>> secondLine) {
        double x1 = firstLine.getFirst().getFirst();
        double y1 = firstLine.getFirst().getSecond();
        double x2 = firstLine.getSecond().getFirst();
        double y2 = firstLine.getSecond().getSecond();

        double x3 = secondLine.getFirst().getFirst();
        double y3 = secondLine.getFirst().getSecond();
        double x4 = secondLine.getSecond().getFirst();
        double y4 = secondLine.getSecond().getSecond();

        double xc = ((x1*y2 - y1*x2) * (x3 - x4) - (x1 - x2) * (x3*y4 - y3*x4)) /
                ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
        double yc = ((x1*y2 - y1*x2) * (y3 - y4) - (y1 - y2) * (x3*y4 - y3*x4)) /
                ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

        if (((xc >= x1 && xc <= x2) || (xc <= x1 && xc >= x2)) &&
                ((xc >= x3 && xc <= x4) || (xc <= x3 && xc >= x4)) &&
                ((yc >= y1 && yc <= y2) || (yc <= y1 && yc >= y2)) &&
                ((yc >= y3 && yc <= y4) || (yc <= y3 && yc >= y4))) {
            return new Pair<>(xc, yc);
        } else {
            return null;
        }
    }

    /**
     * Detects whether the hitboxes of 2 objects (represented by rectangles) have collided.
     * WARNING: Please input the (x,y) position of each vertex starting from the top left one, and then moving clockwise
     * We understand top left as the front left headlight of a car
     * @param firstRectangle The points of the first rectangle. Please read warning
     * @param positionDelta The difference in position from the last frame of the first object
     * @param secondRectangle The points of the second rectangle. Please read warning
     * @return The contact point, and the n vector for collision response
     */
    public static Pair<Pair<Double, Double>,Pair<Double, Double>> hitboxesCollided(List<Pair<Double, Double>> firstRectangle,
                                                                                   Pair<Double, Double> positionDelta,
                                                                                   List<Pair<Double, Double>> secondRectangle,
                                                                                   double secondObjectRotation) {
        assert (firstRectangle.size() == 4 && secondRectangle.size() == 4);

        List<Pair<Double, Double>> oldFirstRectangle = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            oldFirstRectangle.add(new Pair<>(firstRectangle.get(i).getFirst() - positionDelta.getFirst(), firstRectangle.get(i).getSecond() - positionDelta.getSecond()));
        }

        double minimumDistance = 1000000000000.0;
        Pair<Double, Double> contactPoint = null;
        int edgeOnSecondObject = -1;

        for (int i = 0; i < 4; i++) {
            Pair<Pair<Double, Double>, Pair<Double, Double>> movement = new Pair<>(oldFirstRectangle.get(i), firstRectangle.get(i));


            for (int j = 0; j < 4; j++) {
                Pair<Pair<Double, Double>, Pair<Double, Double>> secondObjectLine = new Pair<>(secondRectangle.get(i % 4), secondRectangle.get((i + 1) % 4));

                Pair<Double, Double> newIntersection = linesIntersect(movement, secondObjectLine);
                if (newIntersection != null) {
                    double x = newIntersection.getFirst() - oldFirstRectangle.get(i).getFirst();
                    double y = newIntersection.getSecond() - oldFirstRectangle.get(i).getSecond();
                    double distance = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0));
                    if (distance < minimumDistance) {
                        contactPoint = newIntersection;
                        minimumDistance = distance;
                        edgeOnSecondObject = j;
                    }
                }
            }
        }

        Pair<Double, Double> n = new Pair<>(0.0, 0.0);
        n.setFirst(1.0);
        if (contactPoint != null) {
            if (edgeOnSecondObject == 0) {
                n.setSecond(secondObjectRotation + 180);
            } else if (edgeOnSecondObject == 1) {
                n.setSecond(secondObjectRotation + 90);
            } else if (edgeOnSecondObject == 2) {
                n.setSecond(secondObjectRotation);
            } else if (edgeOnSecondObject == 3) {
                n.setSecond(secondObjectRotation - 90);
            } else {
                System.out.println("Something went wrong in line collisions");
                n.setSecond(0.0);
            }
            return new Pair<>(contactPoint, n);
        } else {
            return null;
        }
    }

    /**
     * Returns true if the objects have collided, false otherwise
     * @param firstObject The first object to be checked
     * @param secondObject The second object to be checked
     * @return True if the objects have collided, false otherwise
     */
    public static Pair<Pair<Double, Double>,Pair<Double, Double>> objectsCollided(Collidable firstObject, Collidable secondObject) {
        Pair<Pair<Double, Double>,Pair<Double, Double>> haveCollided;

        List<List<Pair<Double, Double>>> firstObjectHitboxes = firstObject.getCornersOfAllHitBoxes();
        List<List<Pair<Double, Double>>> secondObjectHitboxes = secondObject.getCornersOfAllHitBoxes();

        for (int i = 0; i < firstObjectHitboxes.size(); i++) {
            for (int j = i; j < secondObjectHitboxes.size(); j++) {
                haveCollided = hitboxesCollided(firstObjectHitboxes.get(i), firstObject.getPositionDelta(), secondObjectHitboxes.get(j), secondObject.getRotation());
                if (haveCollided != null) {
                    return haveCollided;
                }
            }
        }
        return null;
    }

}
