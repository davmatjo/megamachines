package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.util.Pair;

import java.util.Queue;

public class Driver {

    private static final float MARKER_DISTANCE_THRESHOLD = 1f;
    private static final float SPEED_TARGET_MULTIPLIER = 1f;
    private static final double STEERING_DEADZONE = 0.05f;
    private final Queue<Pair<Float, Float>> markers;
    private Pair<Float, Float> currentMarker;
    private final RWDCar car;

    public Driver(TrackRoute route, RWDCar car) {
        this.markers = route.getMarkers();
        this.car = car;
        this.currentMarker = markers.poll();
    }

    public void update() {
        double distance = distanceToMarker();
        if (distance < MARKER_DISTANCE_THRESHOLD) {
            currentMarker = markers.poll();
        }
        double speedTarget = distance * SPEED_TARGET_MULTIPLIER;
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
        }
    }

    private double relativeAngleToMarker() {
        return 0.0;
    }

    private double distanceToMarker() {
        return Math.pow(currentMarker.getFirst() - car.getXInPixels(), 2.0) +
                Math.pow(currentMarker.getSecond() - car.getYInPixels(), 2.0);
    }

    private void steerLeft() {

    }

    private void steerRight() {

    }

    private void accelerate() {

    }

    private void brake() {

    }
}
