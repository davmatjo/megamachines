package com.battlezone.megamachines.world;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Model;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.renderer.game.TrackSet;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Race {

    private final Renderer renderer;
    // TODO: Placeholder for now, needs to be retrieved from the new and improved Track class.
    private final Track[][] trackGrid = new Track[10][10];
    private final float TRACK_PERCENTAGE;
    private TreeMap<RWDCar, Float> carPercentages = new TreeMap<>();
    private HashMap<RWDCar, Track> targetPiece = new HashMap<>();
    private HashMap<RWDCar, Integer> carLaps = new HashMap<>();

    public Race(List<Track> trackPieces, List<RWDCar> cars, Camera cam) {
        TRACK_PERCENTAGE = 1f / trackPieces.size();
        renderer = new Renderer(cam);
        TrackSet trackSet = new TrackSet(Model.generateCar(), cam);
        trackSet.setTrack(trackPieces);
        renderer.addRenderable(trackSet);
        for (RWDCar car : cars) {
            carPercentages.put(car, calculatePosition(car));
            carLaps.put(car, 0);
        }
    }

    public void updatePositioning() {
        for (RWDCar car : carPercentages.keySet())
            carPercentages.put(car, calculatePosition(car));
    }

    private float calculatePosition(RWDCar car) {
        final Track nextTrack = targetPiece.get(car);
        final float approxDist = approximateDistance(car.getXf(), car.getYf(), nextTrack.getXf(), nextTrack.getYf());
        return 0;
    }

    private float approximateDistance(float x1, float y1, float x2, float y2) {
        final float x = x1 - x2;
        final float y = y1 - y2;
        return (x * x) + (y * y);
    }


}
