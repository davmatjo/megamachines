package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Driver {

    private static final float MARKER_DISTANCE_THRESHOLD = 35f;
    private static final float SPEED_TARGET_MULTIPLIER = 0.07f;
    private static final double STEERING_DEADZONE = 0.05f;
    private static final float OFFSET = ScaleController.TRACK_SCALE / 3;
    private Pair<Float, Float> currentMarker;
    private final RWDCar car;
    private final Race race;
    private final List<TrackPiece> pieces;
    private final Map<TrackPiece, Pair<Float, Float>> nextPieces = new HashMap<>();

    public Driver(Track track, RWDCar car, Race race) {
        this.pieces = track.getPieces();
        this.car = car;
        this.race = race;
        currentMarker = new Pair<>(0f, 0f);
        populateMappings();
        setNextMarker();
    }

    private void populateMappings() {
        int startIndex = 0;
        while (pieces.get(startIndex).getType().isStraight()) {
            startIndex = MathUtils.wrap(startIndex - 1, 0, pieces.size());
        }
        TrackPiece currentTarget = pieces.get(startIndex);
        int i = MathUtils.wrap(startIndex - 1, 0, pieces.size());
        // Found a corner to start with
        while (i != startIndex) {
            TrackPiece thisPiece = pieces.get(i);
            nextPieces.put(thisPiece, calcOffset(currentTarget));
            if (thisPiece.getType().isCorner()) {
                currentTarget = thisPiece;
            }
            i = MathUtils.wrap(i - 1, 0, pieces.size());
        }
    }

    private Pair<Float, Float> calcOffset(TrackPiece piece) {
        assert piece.getType().isCorner();
        switch (piece.getType()) {
            case UP_LEFT:
            case RIGHT_DOWN:
                return bottomLeft(piece.getXf(), piece.getYf());
            case UP_RIGHT:
            case LEFT_DOWN:
                return bottomRight(piece.getXf(), piece.getYf());
            case RIGHT_UP:
            case DOWN_LEFT:
                return topLeft(piece.getXf(), piece.getYf());
            case LEFT_UP:
            case DOWN_RIGHT:
            default:
                return topRight(piece.getXf(), piece.getYf());
        }
    }

    private static Pair<Float, Float> topLeft(float x, float y) {
        return new Pair<Float, Float>(x - OFFSET, y + OFFSET);
    }

    private static Pair<Float, Float> topRight(float x, float y) {
        return new Pair<Float, Float>(x + OFFSET, y + OFFSET);
    }

    private static Pair<Float, Float> bottomLeft(float x, float y) {
        return new Pair<Float, Float>(x - OFFSET, y - OFFSET);
    }

    private static Pair<Float, Float> bottomRight(float x, float y) {
        return new Pair<Float, Float>(x + OFFSET, y - OFFSET);
    }

    public void update() {
        currentMarker = nextPieces.getOrDefault(race.getTrackPiece(car), currentMarker);
        double distance = distanceToMarker();

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
        car.setTurnAmount(0.7);
    }

    private void steerRight() {
        car.setTurnAmount(-0.7);
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
        for (int i = MathUtils.wrap(index + 1, 0, pieces.size()); true; i = MathUtils.wrap(++i, 0, pieces.size())) {
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
