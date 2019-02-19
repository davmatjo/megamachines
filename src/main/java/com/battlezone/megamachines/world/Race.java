package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.renderer.game.animation.FallAnimation;
import com.battlezone.megamachines.util.ComparableTriple;
import com.battlezone.megamachines.util.ValueSortedMap;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Race {

    private final int LAP_COUNT;
    private final TrackPiece[][] TRACK_GRID;
    private List<RWDCar> carList;
    private boolean raceFinished = false;

    // Attributes regarding track dimensions/properties
    private final float TRACK_SCALE;
    private final int GRID_MAX_X, GRID_MAX_Y, TRACK_COUNT;
    private final int GRID_MIN_X = 0, GRID_MIN_Y = 0;

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

    // Key track pieces
    private final TrackPiece AFTER_START_PIECE, START_PIECE;

    public static String[] positions = new String[]{"1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th"};

    public Race(Track track, int laps, List<RWDCar> cars) {
        final List<TrackPiece> trackPieces = track.getPieces();

        LAP_COUNT = laps;
        // Populate track grid dimension attributes
        TRACK_GRID = track.getPieceGrid();
        TRACK_SCALE = ScaleController.TRACK_SCALE;
        GRID_MAX_X = track.getTracksAcross() - 1;
        GRID_MAX_Y = track.getTracksDown() - 1;
        TRACK_COUNT = trackPieces.size();

        // Get key track pieces
        START_PIECE = track.getStartPiece();
        AFTER_START_PIECE = trackPieces.get(MathUtils.wrap(trackPieces.indexOf(START_PIECE) + 1, 0, TRACK_COUNT));

        carList = cars;

        for (int i = 0; i < TRACK_COUNT; i++) {
            // Populate the track pieces to percentage map
            trackNumber.put(trackPieces.get(i), i);
            // Populate the track to next track map
            nextTrack.put(trackPieces.get(i), trackPieces.get(MathUtils.wrap(i + 1, 0, TRACK_COUNT)));
        }

        // Populate laps and positions of cars
        for (RWDCar car : cars) {
            carTrackPosition.put(car, getPhysicalPosition(car));
            carLap.put(car, 0);
            carPosition.put(car, calculatePosition(car, new ComparableTriple<>(0, 0, 0d)));
        }
    }

    public void update() {
        for (RWDCar car : carList) {
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
    }

    private TrackPiece getPhysicalPosition(RWDCar car) {
        // Scale coordinates down to track grid, clamping min and max
        final int gridX = (int) Math.round(car.getX() / TRACK_SCALE);
        final int gridY = (int) Math.round(car.getY() / TRACK_SCALE);
        // Out of range
        if (!(MathUtils.inRange(gridX, GRID_MIN_X, GRID_MAX_X) && MathUtils.inRange(gridY, GRID_MIN_Y, GRID_MAX_Y))) {
            final TrackPiece piece = carTrackPosition.get(car);
            fallOff(car, piece);
            return piece;
        } else {
            final int carGridX = MathUtils.clamp(gridX, GRID_MIN_X, GRID_MAX_X);
            final int carGridY = MathUtils.clamp(gridY, GRID_MIN_Y, GRID_MAX_Y);
            return TRACK_GRID[carGridX][carGridY];
        }
    }

    private void fallOff(RWDCar car, TrackPiece correctPiece) {
        car.playAnimation(FallAnimation.class);
        car.setX(correctPiece.getX());
        car.setY(correctPiece.getY());
        car.setSpeed(0);
        car.setAngle(correctPiece.getType().getAngle());
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
        if (previousPos.equals(START_PIECE) && currentPos.equals(AFTER_START_PIECE)) {
            // They've gone past the start, increase lap counter
            laps = carLap.get(car) + 1;
            carLap.put(car, laps);
            increasedLap(laps, car);
        } else if (previousPos.equals(AFTER_START_PIECE) && currentPos.equals(START_PIECE)) {
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
        final int dist = TRACK_COUNT * (laps - 1) + trackNumber.get(currentPos);
        final TrackPiece nextPiece = nextTrack.get(currentPos);
        final double distToNext = MathUtils.distanceSquared(nextPiece.getX(), nextPiece.getY(), car.getX(), car.getY());

        // Put into the pair, negate distance so smaller distances are greater values
        pair.set(laps, dist, -distToNext);
        return pair;
    }

    private void increasedLap(int newLap, RWDCar car) {
        if (newLap == LAP_COUNT) {
            freezePosition(car);
        }
    }

    private void freezePosition(RWDCar car) {

    }

    private void decreasedLap(int newLap, RWDCar car) {

    }

    public boolean hasFinished() {
        return raceFinished;
    }

}
