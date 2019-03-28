package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.networking.server.game.GameRoom;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The driver class drives a car using predetermined rules
 */
public class Driver {

    /**
     * The list of possible names to choose from for the AI drivers.
     */
    public static final String[] names = {"Sebastian", "David", "Hamzah", "Kieran", "Stefan", "Claire", "Charlie", "Ewan", "Ben", "Jack", "Alex", "Stephanie", "Andreea", "Natalie", "Krisitan", "Megan", "Adam", "Gintare", "Giorgios", "Ian", "Benny"};

    /**
     * The aggressiveness of speed change depending on how far away from the next marker the car is
     */
    private static final float SPEED_TARGET_MULTIPLIER = 0.07f;

    /**
     * The car will not turn if the angle between it and the marker is within this threshold
     */
    private static final double STEERING_DEADZONE = 0.05f;

    /**
     * Offset from the center of the track for marker placement
     */
    private static final float OFFSET = ScaleController.TRACK_SCALE / 3;

    /**
     * Random for placing markers with noise
     */
    private static final Random RAND = new Random();

    /**
     * The maximum variation in marker placement
     */
    private static final float MAX_NOISE = OFFSET / 2;
    /**
     * The maximum interval at which the AI checks for powerups
     */
    private final static double POWERUP_INTERVAL = 5;
    /**
     * The car that this driver is driving
     */
    private final RWDCar car;

    /**
     * The race that this driver is taking part in
     */
    private final Race race;

    /**
     * The pieces of track that form the track this driver is driving on
     */
    private final List<TrackPiece> pieces;

    /**
     * A map from all pieces to which marker the driver should be driving towards
     */
    private final Map<TrackPiece, Pair<Float, Float>> nextPieces = new HashMap<>();
    /**
     * The marker the driver is currently attempting to drive to
     */
    private Pair<Float, Float> currentMarker;
    /**
     * The timer variables for the AI's powerups
     */
    private double timer = RAND.nextDouble() * POWERUP_INTERVAL, elapsed = 0;

    /**
     * Used to communicate back to the server if in multiplayer
     */
    private GameRoom multiplayer;

    /**
     * Creates a new driver
     *
     * @param track The track to drive on
     * @param car   The car this driver will drive
     * @param race  The race this driver will drive in
     */
    public Driver(Track track, RWDCar car, Race race) {
        this.pieces = track.getPieces();
        this.car = car;
        car.setDriver(this);
        this.race = race;
        currentMarker = new Pair<>(0f, 0f);
        populateMappings();
    }

    /**
     * Works out a valid marker in the top left region of the track piece
     *
     * @param x x of the piece
     * @param y y of the piece
     * @return A valid marker for a piece of this type in this position
     */
    private static Pair<Float, Float> topLeft(float x, float y) {
        return new Pair<>(x - OFFSET + generateNoise(), y + OFFSET - generateNoise());
    }

    /**
     * Works out a valid marker in the top right region of the track piece
     *
     * @param x x of the piece
     * @param y y of the piece
     * @return A valid marker for a piece of this type in this position
     */
    private static Pair<Float, Float> topRight(float x, float y) {
        return new Pair<>(x + OFFSET - generateNoise(), y + OFFSET - generateNoise());
    }

    /**
     * Works out a valid marker in the bottom left region of the track piece
     *
     * @param x x of the piece
     * @param y y of the piece
     * @return A valid marker for a piece of this type in this position
     */
    private static Pair<Float, Float> bottomLeft(float x, float y) {
        return new Pair<>(x - OFFSET + generateNoise(), y - OFFSET + generateNoise());
    }

    /**
     * Works out a valid marker in the bottom right region of the track piece
     *
     * @param x x of the piece
     * @param y y of the piece
     * @return A valid marker for a piece of this type in this position
     */
    private static Pair<Float, Float> bottomRight(float x, float y) {
        return new Pair<>(x + OFFSET - generateNoise(), y - OFFSET + generateNoise());
    }

    private static float generateNoise() {
        return RAND.nextFloat() * MAX_NOISE;
    }

    /**
     * A method to set the AI's multiplayer connection
     *
     * @param room the connection to set
     */
    public void setGameroom(GameRoom room) {
        multiplayer = room;
    }

    /**
     * Populates the nextPieces map based on the track data
     */
    private void populateMappings() {
        if (pieces.size() == 0) {
            return;
        }
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

    /**
     * Calculates the correct offset to place the marker at depending on the piece type
     *
     * @param piece track piece to calculate with
     * @return A coordinate for the marker
     * @see Pair
     */
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

    /**
     * Get the driver to update its decisions for this frame
     *
     * @param interval the interval since the last update call, in seconds
     */
    public void update(double interval) {
        // Update powerup check timer
        elapsed += interval;

        // Get the piece the driver is aiming for and how far away it is
        currentMarker = nextPieces.getOrDefault(race.getTrackPiece(car), currentMarker);
        double distance = distanceToMarker();

        // Work out what speed we should be going and accelerate/brake accordingly
        double speedTarget = MathUtils.clampd(distance * SPEED_TARGET_MULTIPLIER, 7.0, 15.0);
        if (car.getSpeed() > speedTarget) {
            brake();
        } else {
            accelerate();
        }

        // Work out whether we need to steer left or right or not at all to point at the marker
        var relativeAngleToMarker = relativeAngleToMarker();
        if (relativeAngleToMarker > 0 + STEERING_DEADZONE) {
            steerLeft();
        } else if (relativeAngleToMarker < 0 - STEERING_DEADZONE) {
            steerRight();
        } else {
            steerNone();
        }

        // Check whether the AI is able to check for a powerup
        if (elapsed >= timer) {
            // AI able to activate powerup
            final Powerup powerup = car.getCurrentPowerup();
            if (powerup != null) {
                if (multiplayer != null)
                    multiplayer.sendPowerup(car);
                powerup.activate();
            }
            // Reset timer back to a random interval
            timer = RAND.nextDouble() * POWERUP_INTERVAL;
            elapsed = 0;
        }

    }

    /**
     * Works out the relative angle between the car's angle and the line projected from the car to the next marker
     *
     * @return the angle
     */
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

    /**
     * Pythagoras without the square root for added speed
     *
     * @return a distance, relatively
     */
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

    /**
     * Works out the difference between 2 angles, normalised in the range 0 - 2Pi
     *
     * @param a1 The first angle
     * @param a2 The second angle
     * @return A normalised difference between a1 and a2
     */
    private double angleDifference(double a1, double a2) {
        double r = (a2 - a1) % (2 * Math.PI);
        if (r < -Math.PI)
            r += 2 * Math.PI;
        if (r >= Math.PI)
            r -= 2 * Math.PI;
        return r;
    }

    /**
     * Normalised an angle to the range -Pi to Pi
     *
     * @return The normalised angle
     */
    private double getNormalisedAngle() {
        double angle = car.getAngle() % 360;
        return angle < 0 ? Math.toRadians(angle + 180) : Math.toRadians(angle - 180);
    }
}
