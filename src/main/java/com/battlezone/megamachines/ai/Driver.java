package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Driver {

    private static final float MARKER_DISTANCE_THRESHOLD = 35f;
    private static final float SPEED_TARGET_MULTIPLIER = 0.07f;
    private static final double STEERING_DEADZONE = 0.05f;
    private Pair<Float, Float> currentMarker;
    private final RWDCar car;
    private final Race race;
    private final List<TrackPiece> pieces;

    public Driver(Track track, RWDCar car, Race race) {
        this.pieces = track.getPieces();
        this.car = car;
        this.race = race;
        currentMarker = new Pair<>(0f, 0f);
        setNextMarker();
    }

    public void update() {
        double distance = distanceToMarker();
//        System.out.println(car.getX() + ", " + car.getY() + " ][ " + currentMarker.toString() + " ][ " + relativeAngleToMarker() + " ][ " + getNormalisedAngle());
        if (distance < MARKER_DISTANCE_THRESHOLD) {
//            System.out.println("Next marker");
            setNextMarker();
        }

        double speedTarget = MathUtils.clampd(distance * SPEED_TARGET_MULTIPLIER, 7.0, 15.0);
//        System.out.println("[ " + car.getSpeed() + " ][ " + speedTarget  + " ]");
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

    private void setNextMarker() {
        TrackPiece piece = race.getTrackPiece(car);
        int index = pieces.indexOf(piece);
        for (int i=MathUtils.wrap(index + 1, 0, pieces.size()); true; i = MathUtils.wrap(++i, 0, pieces.size())) {
            TrackPiece toTest = pieces.get(i);
            if (toTest.getType().equals(TrackType.UP_RIGHT) ||
                    toTest.getType().equals(TrackType.UP_LEFT) ||
                    toTest.getType().equals(TrackType.DOWN_RIGHT) ||
                    toTest.getType().equals(TrackType.DOWN_LEFT) ||
                    toTest.getType().equals(TrackType.LEFT_UP) ||
                    toTest.getType().equals(TrackType.LEFT_DOWN) ||
                    toTest.getType().equals(TrackType.RIGHT_UP) ||
                    toTest.getType().equals(TrackType.RIGHT_DOWN)) {
                currentMarker.setFirst(toTest.getXf());
                currentMarker.setSecond(toTest.getYf());
                break;
            }
        }
    }

    public void fallen() {
        setNextMarker();
    }
}
