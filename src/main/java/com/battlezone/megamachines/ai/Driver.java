package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.util.Pair;
import org.lwjgl.system.CallbackI;

import java.util.Queue;

public class Driver {

    private static final float MARKER_DISTANCE_THRESHOLD = 35f;
    private static final float SPEED_TARGET_MULTIPLIER = 0.07f;
    private static final double STEERING_DEADZONE = 0.05f;
    private Queue<Pair<Float, Float>> markers;
    private Pair<Float, Float> currentMarker;
    private final RWDCar car;
    private final TrackRoute route;

    public Driver(TrackRoute route, RWDCar car) {
        this.route = route;
        this.markers = route.getMarkers();
        this.car = car;
        this.currentMarker = markers.poll();
        System.out.println(markers);
    }

    public void update() {
        double distance = distanceToMarker();
//        System.out.println(car.getX() + ", " + car.getY() + " ][ " + currentMarker.toString() + " ][ " + relativeAngleToMarker() + " ][ " + getNormalisedAngle());
        if (distance < MARKER_DISTANCE_THRESHOLD) {
            System.out.println("Next marker");
            if (markers.isEmpty()) {
                markers = route.getMarkers();
            }
            currentMarker = markers.poll();
        }
        double speedTarget = Math.max(distance * SPEED_TARGET_MULTIPLIER, 9.0);
        speedTarget = Math.min(17.0, speedTarget);
        System.out.println("[ " + car.getSpeed() + " ][ " + speedTarget  + " ]");
        if (car.getSpeed() > speedTarget) {
            brake();
        } else {
            accelerate();
        }

        var relativeAngleToMarker = relativeAngleToMarker();
        if (relativeAngleToMarker > 0 + STEERING_DEADZONE) {
            steerLeft();
        } else if (relativeAngleToMarker < 0 - STEERING_DEADZONE) {
            steerRight();
        } else {
            steerNone();
        }
    }

    private double relativeAngleToMarker() {
        double relX = car.getX() - currentMarker.getFirst();
        double relY = car.getY() - currentMarker.getSecond();
        if (relX > 0) {
            if (relY > 0) {
                return angleDifference(getNormalisedAngle(), Math.atan(relY / relX));
            } else {
                return angleDifference(getNormalisedAngle(), 2 * Math.PI - Math.atan(-relY / relX));
            }
        } else {
            if (relY > 0) {
                return angleDifference(getNormalisedAngle(), Math.PI - Math.atan(relY / -relX));
            } else {
                return angleDifference(getNormalisedAngle(), Math.PI + Math.atan(-relY / -relX));
            }
        }
    }

    private double distanceToMarker() {
        return Math.pow(currentMarker.getFirst() - car.getX(), 2.0) +
                Math.pow(currentMarker.getSecond() - car.getY(), 2.0);
    }

    private void steerLeft() {
        car.setTurnAmount(1.0);
//        System.out.println("left");
    }

    private void steerRight() {
        car.setTurnAmount(-1.0);
//        System.out.println("right");
    }

    private void steerNone() {
        car.setTurnAmount(0.0);
    }

    private void accelerate() {
        car.setAccelerationAmount(1.0);
        car.setBrakeAmount(0.0);
    }

    private void brake() {
        car.setBrakeAmount(1.0);
        car.setAccelerationAmount(0.0);
    }

    private double angleDifference(double a1, double a2) {
        double r = (a2 - a1) % (2 * Math.PI);
        if (r < -Math.PI)
            r += 2 * Math.PI;
        if (r >= Math.PI)
            r -= 2 * Math.PI;
        return r;
    }

    private double getNormalisedAngle() {
        double angle = car.getAngle() % 360;
        return angle < 0 ? Math.toRadians(angle + 180) : Math.toRadians(angle - 180);
    }
}
