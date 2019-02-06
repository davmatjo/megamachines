package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.util.ComparablePair;
import com.battlezone.megamachines.util.ValueSortedMap;

import java.util.HashMap;
import java.util.List;

public class Race {

    private final int LAP_COUNT;
    private final TrackPiece[][] TRACK_GRID;
    private List<RWDCar> carList;

    // Attributes regarding track dimensions/properties
    private final int TRACK_SCALE, TRACK_MAX_X, TRACK_MAX_Y, TRACK_COUNT;
    private final int TRACK_MIN_X = 0, TRACK_MIN_Y = 0;

    // Stores the percentage of the car's distance around the track
    private ValueSortedMap<RWDCar, ComparablePair<Integer, Double>> carPosition = new ValueSortedMap<>();
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

    public Race(Track track, int laps, List<RWDCar> cars) {
        final List<TrackPiece> trackPieces = track.getPieces();

        LAP_COUNT = laps;
        // Populate track grid dimension attributes
        TRACK_GRID = track.getPieceGrid();
        TRACK_SCALE = track.getTrackSize();
        TRACK_MAX_X = track.getTracksAcross() * TRACK_SCALE;
        TRACK_MAX_Y = track.getTracksDown() * TRACK_SCALE;
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
            carPosition.put(car, calculatePosition(car));
            carLap.put(car, 0);
        }
    }

    public void update() {
        for (RWDCar car : carList)
            carPosition.put(car, calculatePosition(car));
        System.out.println(carList);
    }

    private TrackPiece getPhysicalPosition(RWDCar car) {
        // Scale coordinates down to track grid, clamping min and max
        final int carGridX = MathUtils.clamp((int) (car.getXf() / TRACK_SCALE), TRACK_MIN_X, TRACK_MAX_X);
        final int carGridY = MathUtils.clamp((int) (car.getYf() / TRACK_SCALE), TRACK_MIN_Y, TRACK_MAX_Y);
        return TRACK_GRID[carGridX][carGridY];
    }

    private ComparablePair<Integer, Double> calculatePosition(RWDCar car) {
        final TrackPiece previousPos = carTrackPosition.get(car);
        final TrackPiece currentPos = getPhysicalPosition(car);

        // Update & get laps
        final int laps;
        if (previousPos.equals(START_PIECE) && currentPos.equals(AFTER_START_PIECE))
            // They've gone past the start, increase lap counter
            laps = carLap.put(car, carLap.get(car) + 1);
        else if (previousPos.equals(AFTER_START_PIECE) && currentPos.equals(START_PIECE))
            // They've gone backwards, decrease lap counter
            laps = carLap.put(car, carLap.get(car) - 1);
        else
            // No significant change, get laps
            laps = carLap.get(car);

        // Update car's physical position
        carTrackPosition.put(car, currentPos);

        // Get a distance value for the car overall
        final int dist = TRACK_COUNT * (laps - 1) + trackNumber.get(currentPos);
        final TrackPiece nextPiece = nextTrack.get(currentPos);
        final double distToNext = MathUtils.distanceSquared(nextPiece.getX(), nextPiece.getY(), car.getX(), car.getY());

        // Put into a comparable pair
        return new ComparablePair<Integer, Double>(dist, distToNext);
    }

}
