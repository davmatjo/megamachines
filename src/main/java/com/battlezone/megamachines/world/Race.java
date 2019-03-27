package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector2d;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.renderer.game.animation.LandAnimation;
import com.battlezone.megamachines.util.ComparableTriple;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.util.ValueSortedMap;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.util.*;

public class Race {

    private final static double END_TIMER = 30;
    public static String[] positions = new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"};
    private final int lapCount;
    private final TrackPiece[][] trackGrid;
    // Attributes regarding track dimensions/properties
    private final float trackScale;
    private final int gridMaxX, gridMaxY, trackCount;
    private final int gridMinX = 0, gridMinY = 0;
    // Key track pieces
    private final TrackPiece beforeFinish, finishPiece;
    private List<RWDCar> carList;
    private boolean raceFinished = false;
    private double raceEnd = Double.MAX_VALUE;
    // Stores the lap, track number and distance to next track piece
    private ValueSortedMap<RWDCar, ComparableTriple<Integer, Integer, Double>> carPosition = new ValueSortedMap<>();
    // Stores the track piece of which the car is currently on
    private HashMap<RWDCar, TrackPiece> carTrackPosition = new HashMap<>();
    // Stores the cars' lap counters
    private HashMap<RWDCar, Integer> carLap = new HashMap<>();
    // Stores the next track piece for a given track piece
    private HashMap<TrackPiece, TrackPiece> nextTrack = new HashMap<>();
    // Stores a mapping from track piece to its number around the track (0 = start, X = end)
    private HashMap<TrackPiece, Integer> trackNumber = new HashMap<>();
    // Finalised positions
    private List<RWDCar> finalPositions = new ArrayList<>();
    // HashSet of finalised positions for quick access
    private HashSet<RWDCar> finalPositionsSet = new HashSet<>();
    // List of track pieces
    private List<TrackPiece> trackList;
    // Recently finished player
    private Pair<RWDCar, Byte> recentlyFinished = new Pair<>(null, (byte) 0),
            bufferFinished = new Pair<>(null, (byte) 0);

    public Race(Track track, int laps, List<RWDCar> cars) {
        final List<TrackPiece> trackPieces = track.getPieces();

        lapCount = laps;
        // Populate track grid dimension attributes
        trackGrid = track.getPieceGrid();
        trackScale = ScaleController.TRACK_SCALE;
        gridMaxX = track.getTracksAcross() - 1;
        gridMaxY = track.getTracksDown() - 1;
        trackCount = trackPieces.size();
        trackList = trackPieces;

        // Get key track pieces
        finishPiece = track.getFinishPiece();
        beforeFinish = trackPieces.get(MathUtils.wrap(trackPieces.indexOf(finishPiece) - 1, 0, trackCount));

        carList = cars;

        for (int i = 0; i < trackCount; i++) {
            // Populate the track pieces to position map
            trackNumber.put(trackPieces.get(i), i);
            // Populate the track to next track map
            nextTrack.put(trackPieces.get(i), trackPieces.get(MathUtils.wrap(i + 1, 0, trackCount)));
        }

        // Populate laps and positions of cars
        for (RWDCar car : cars) {
            carTrackPosition.put(car, getPhysicalPosition(car));
            carLap.put(car, 0);
            carPosition.put(car, calculatePosition(car, new ComparableTriple<>(0, 0, 0d)));
        }
    }

    public void update() {
        for (int i = 0; i < carList.size(); i++) {
            RWDCar car = carList.get(i);
            final ComparableTriple<Integer, Integer, Double> pos = calculatePosition(car, carPosition.get(car));
            carPosition.put(car, pos);
        }
        int counter = 0;
        // Set of cars is in ASCENDING order of position (lowest to highest)
        final Set<RWDCar> cars = carPosition.keySet();
        for (RWDCar car : cars) {
            counter++;
            // No need to tell the car its position if it's finished
            if (finalPositionsSet.contains(car))
                continue;
            // Calculate position in reverse
            car.setPosition((byte) (cars.size() - counter));
            car.setLap(carLap.get(car).byteValue());
        }
        if (System.nanoTime() >= raceEnd) {
            raceFinished = true;
            for (RWDCar car : carPosition.keySet())
                freezePosition(car);
        }
    }

    public TrackPiece getTrackPiece(RWDCar car) {
        return getTrackPiece(car.getX(), car.getY());
    }

    private TrackPiece getTrackPiece(final double x, final double y) {
        // Scale coordinates down to track grid, clamping min and max
        final int gridX = (int) Math.round(x / trackScale);
        final int gridY = (int) Math.round(y / trackScale);

        // Out of range
        if (!(MathUtils.inRange(gridX, gridMinX, gridMaxX) && MathUtils.inRange(gridY, gridMinY, gridMaxY))) {
            return null;
        } else {
            final int carGridX = MathUtils.clamp(gridX, gridMinX, gridMaxX);
            final int carGridY = MathUtils.clamp(gridY, gridMinY, gridMaxY);
            TrackPiece piece = trackGrid[carGridX][carGridY];

            if (piece != null) {
                float pieceX = piece.getXf();
                float pieceY = piece.getYf();
                float cornerDist = piece.getScale() / 2f;
                switch (piece.getType()) {
                    case LEFT_UP:
                    case DOWN_RIGHT:
                        if (pointInTriangle(x, y,
                                pieceX - cornerDist, pieceY - cornerDist,
                                pieceX - cornerDist, pieceY - cornerDist + 10 * cornerDist / 16f,
                                pieceX - cornerDist + 10 * cornerDist / 16f, pieceY - cornerDist)) {
                            return null;
                        }
                        break;
                    case RIGHT_UP:
                    case DOWN_LEFT:
                        if (pointInTriangle(x, y,
                                pieceX + cornerDist, pieceY - cornerDist,
                                pieceX + cornerDist, pieceY - cornerDist + 10 * cornerDist / 16f,
                                pieceX + cornerDist - 10 * cornerDist / 16f, pieceY - cornerDist)) {
                            return null;
                        }
                        break;
                    case LEFT_DOWN:
                    case UP_RIGHT:
                        if (pointInTriangle(x, y,
                                pieceX - cornerDist, pieceY + cornerDist,
                                pieceX - cornerDist, pieceY + cornerDist - 10 * cornerDist / 16f,
                                pieceX - cornerDist + 10 * cornerDist / 16f, pieceY + cornerDist)) {
                            return null;
                        }
                        break;
                    case RIGHT_DOWN:
                    case UP_LEFT:
                        if (pointInTriangle(x, y,
                                pieceX + cornerDist, pieceY + cornerDist,
                                pieceX + cornerDist, pieceY + cornerDist - 10 * cornerDist / 16f,
                                pieceX + cornerDist - 10 * cornerDist / 16f, pieceY + cornerDist)) {
                            return null;
                        }
                        break;
                }
            }
            return piece;
        }
    }

    private TrackPiece getPhysicalPosition(RWDCar car) {
        TrackPiece piece = getTrackPiece(car);
        if (piece == null) {
            piece = carTrackPosition.get(car);
            fallOff(car, carTrackPosition.get(car));
        }
        return piece;
    }

    // Determines whether a point (xp, yp) is within a triangle of points (0, 1, 2)
    private boolean pointInTriangle(double xp, double yp, double x0, double y0, double x1, double y1, double x2, double y2) {
        final double area = 0.5 * (-y1 * x2 + y0 * (-x1 + x2) + x0 * (y1 - y2) + x1 * y2);
        final double s = 1 / (2 * area) * (y0 * x2 - x0 * y2 + (y2 - y0) * xp + (x0 - x2) * yp);
        final double t = 1 / (2 * area) * (x0 * y1 - y0 * x1 + (y0 - y1) * xp + (x1 - x0) * yp);

        return (s > 0 && t > 0 && (1 - s - t) > 0);
    }


    private void fallOff(RWDCar car, TrackPiece correctPiece) {
        // Check if the center of mass is over the edge of the track
        var centerOfMass = car.getCenterOfMassPosition();
        if (getTrackPiece(centerOfMass.x, centerOfMass.y) != null)
            return;

        // Check opposing corners
        var corners = car.getCornersOfAllHitBoxes().get(0);
        Vector2d c1, c2;
        for (int i = 0; i < 2; i++) {
            c1 = corners.get(i);
            c2 = corners.get(2 + i);
            if (getTrackPiece(c1.x, c1.y) != null && getTrackPiece(c2.x, c2.y) != null)
                return;
        }

        if (car.getCurrentlyPlaying() == 0) {
            car.playAnimation(FallAnimation.class, () -> {
                final TrackPiece prev = trackList.get(MathUtils.wrap(trackNumber.get(correctPiece) - 1, 0, trackCount));
                car.setX(prev.getX());
                car.setY(prev.getY());
                car.setSpeed(0);
                car.setAngle(prev.getType().getAngle());
                car.playAnimation(LandAnimation.class);
                car.setControlsActive(true);
            });
            car.setControlsActive(false);
        }
    }

    private ComparableTriple<Integer, Integer, Double> calculatePosition(RWDCar
                                                                                 car, ComparableTriple<Integer, Integer, Double> pair) {
        final TrackPiece previousPos = carTrackPosition.get(car);
        TrackPiece currentPos = getPhysicalPosition(car);

        // Update & get laps
        final int laps;
        if (previousPos.equals(beforeFinish) && currentPos.equals(finishPiece) && !finalPositionsSet.contains(car)) {
            // They've gone past the start, increase lap counter
            laps = carLap.get(car) + 1;
            carLap.put(car, laps);
            increasedLap(laps, car);
        } else if (previousPos.equals(finishPiece) && currentPos.equals(beforeFinish) && !finalPositionsSet.contains(car)) {
            // They've gone backwards, decrease lap counter
            laps = carLap.get(car) - 1;
            carLap.put(car, laps);
            decreasedLap(laps, car);
        } else {
            // No significant change, get laps
            laps = carLap.get(car);
        }

        // Update car's physical position
        carTrackPosition.put(car, currentPos);

        // Get a distance value for the car overall
        final int dist = trackNumber.get(currentPos);
        final TrackPiece nextPiece = nextTrack.get(currentPos);
        final double distToNext = MathUtils.distanceSquared(nextPiece.getX(), nextPiece.getY(), car.getX(), car.getY());

        // Put into the pair, negate distance so smaller distances are greater values
        pair.set(laps, dist, -distToNext);
        return pair;
    }

    private void increasedLap(int newLap, RWDCar car) {
        if (newLap == lapCount + 1) {
            freezePosition(car);
        }
    }

    private void freezePosition(RWDCar car) {
        // If the position hasn't been frozen
        if (!finalPositionsSet.contains(car)) {
            // Add the car to the final position trackers
            finalPositions.add(car);
            finalPositionsSet.add(car);
            // Set its position and lap
            final byte position = (byte) (finalPositions.size() - 1);
            car.setPosition(position);
            recentlyFinished.set(car, position);
            // if 1/3 of the cars are done
            if (raceEnd == Double.MAX_VALUE) {
                if (finalPositions.size() >= carList.size() / 3) {
                    // Start timer
                    raceEnd = System.nanoTime() + MathUtils.secToNan(END_TIMER);
                    System.out.println("COUNTDOWN");
                }
            }
            if (finalPositions.size() == carList.size()) {
                raceEnd = System.nanoTime();
            }
        }
    }

    public Pair<RWDCar, Byte> getRecentlyFinished() {
        if (recentlyFinished.getFirst() == null)
            return null;
        bufferFinished.set(recentlyFinished.getFirst(), recentlyFinished.getSecond());
        recentlyFinished.setFirst(null);
        return bufferFinished;
    }

    public List<RWDCar> getFinalPositions() {
        return finalPositions;
    }

    private void decreasedLap(int newLap, RWDCar car) {

    }

    public boolean hasFinished() {
        return raceFinished;
    }

}
