package com.battlezone.megamachines.world;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.renderer.game.animation.LandAnimation;
import com.battlezone.megamachines.util.ComparableTriple;
import com.battlezone.megamachines.util.ValueSortedMap;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Race {

    private final int lapCount;
    private final TrackPiece[][] trackGrid;
    private List<RWDCar> carList;
    private boolean raceFinished = false;
    private double raceEnd = Double.MAX_VALUE;
    private final static double END_TIMER = 30;

    // Attributes regarding track dimensions/properties
    private final float trackScale;
    private final int gridMaxX, gridMaxY, trackCount;
    private final int gridMinX = 0, gridMinY = 0;

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

    // Key track pieces
    private final TrackPiece beforeFinish, finishPiece;

    public static String[] positions = new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th"};

    public Race(Track track, int laps, List<RWDCar> cars) {
        final List<TrackPiece> trackPieces = track.getPieces();

        lapCount = laps;
        // Populate track grid dimension attributes
        trackGrid = track.getPieceGrid();
        trackScale = ScaleController.TRACK_SCALE;
        gridMaxX = track.getTracksAcross() - 1;
        gridMaxY = track.getTracksDown() - 1;
        trackCount = trackPieces.size();

        // Get key track pieces
        finishPiece = track.getFinishPiece();
        beforeFinish = trackPieces.get(MathUtils.wrap(trackPieces.indexOf(finishPiece) - 1, 0, trackCount));

        carList = cars;

        for (int i = 0; i < trackCount; i++) {
            // Populate the track pieces to percentage map
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
        final Set<RWDCar> cars = carPosition.keySet();
        for (RWDCar car : cars) {
            counter++;
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
            return trackGrid[carGridX][carGridY];
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

    private void fallOff(RWDCar car, TrackPiece correctPiece) {
        // Check if whole car has fallen off
        var corners = car.getCornersOfAllHitBoxes().get(0);
        int cornersOn = 0;
        for (int i=0; i<corners.size(); i++) {
            var corner = corners.get(i);
            if (getTrackPiece(corner.getFirst(), corner.getSecond()) != null) {
                // Don't fall off if 2 corners of the car are on the track
                cornersOn++;
                if (cornersOn >= 2) {
                    return;
                }
            }
        }

        if (car.isControlsActive()) {
            car.playAnimation(FallAnimation.class, () -> {
                car.setX(correctPiece.getX());
                car.setY(correctPiece.getY());
                car.setSpeed(0);
                car.setAngle(correctPiece.getType().getAngle());
                car.playAnimation(LandAnimation.class);
                car.setControlsActive(true);
            });
            car.setControlsActive(false);
        } else {
            car.setSpeed(car.getSpeed() * 0.9);
        }
    }

    private ComparableTriple<Integer, Integer, Double> calculatePosition(RWDCar car, ComparableTriple<Integer, Integer, Double> pair) {
        final TrackPiece previousPos = carTrackPosition.get(car);
        TrackPiece currentPos = getPhysicalPosition(car);

        if (currentPos == null) {
            fallOff(car, previousPos);
            currentPos = previousPos;
        }

        // Update & get laps
        final int laps;
        if (previousPos.equals(beforeFinish) && currentPos.equals(finishPiece)) {
            // They've gone past the start, increase lap counter
            laps = carLap.get(car) + 1;
            carLap.put(car, laps);
            increasedLap(laps, car);
        } else if (previousPos.equals(finishPiece) && currentPos.equals(beforeFinish)) {
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
        if (!finalPositions.contains(car)) {
            finalPositions.add(car);
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

    public List<RWDCar> getFinalPositions() {
        return finalPositions;
    }

    private void decreasedLap(int newLap, RWDCar car) {

    }

    public boolean hasFinished() {
        return raceFinished;
    }

}
