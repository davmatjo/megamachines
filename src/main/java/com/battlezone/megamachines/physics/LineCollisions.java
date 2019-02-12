package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.util.Pair;

public class LineCollisions {
    private static boolean linesIntersect(Pair<Pair<Double, Double>,Pair<Double, Double>> firstLine, Pair<Pair<Double, Double>,Pair<Double, Double>> secondLine) {
        //We want the first point that forms the first line to be the one with the smaller X value
        if (firstLine.getFirst().getFirst() > firstLine.getSecond().getFirst()) {
            Pair<Double, Double> t = firstLine.getFirst();
            firstLine.setFirst(firstLine.getSecond());
            firstLine.setSecond(t);
        }

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

        if (xc >= x1 && xc <= x2) {
            if (yc >= y1 && yc <= y2) {
                return true;
            } else if (yc <= y1 && yc >= y2) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}
