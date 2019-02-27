package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PowerupManager {

    private static final List<Class<? extends Powerup>> POWERUPS = List.of(TestPowerup.class);
    private final Track track;
    private final Map<Powerup, Pair<Double, Double>> activePowerups;


    public PowerupManager(Track track) {
        try {
            Random r = new Random();
            this.activePowerups = new HashMap<>();
            this.track = track;
            List<TrackPiece> pieces = track.getPieces();
            int trackLength = pieces.size();

            for (int i = 0; i < trackLength / 8; i++) {
                int selection = r.nextInt(trackLength);
                var locationLine = getLineFromPiece(pieces.get(selection));
                for (var location : locationLine) {
                    var randomPowerup = POWERUPS.get(r.nextInt(POWERUPS.size()));
                    var randomPowerupConstructor = randomPowerup.getDeclaredConstructor(double.class, double.class, PowerupManager.class);
                    activePowerups.put(randomPowerupConstructor.newInstance(location.getFirst(), location.getSecond(), this), location);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating class. This should not happen");
        }

    }

    private List<Pair<Double, Double>> getLineFromPiece(TrackPiece piece) {
        return null;
    }

    public void destroyPowerup(Powerup toDestroy) {

    }

    public void update() {

    }
}
