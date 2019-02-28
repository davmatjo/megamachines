package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.powerups.powerupTypes.*;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PowerupManager implements Drawable {

    public static final int TRACK_DIVISOR = 16;
    private static final List<Class<? extends Powerup>> POWERUPS = List.of(Agility.class, Bomb.class, FakeItem.class, GrowthPowerup.class, OilSpill.class);
    private static final int POWERUP_BUFFER_SIZE = 100;
    private static final Model model = Model.generateSquare();
    private final Queue<Powerup> randomisedPowerups;
    private final Track track;
    private final List<PowerupSpace> spaces;


    public PowerupManager(Track track) {
        try {
            Random r = new Random();
            this.spaces = new ArrayList<>();
            this.randomisedPowerups = new LinkedList<>();
            this.track = track;
            List<TrackPiece> pieces = track.getPieces();
            int trackLength = pieces.size();

            for (int i=0; i<POWERUP_BUFFER_SIZE; i++) {
                int selection = r.nextInt(POWERUPS.size());
                Class<? extends Powerup> powerup = POWERUPS.get(selection);
                randomisedPowerups.add(powerup.getDeclaredConstructor().newInstance());
            }

            final List<Integer> previousChoices = new ArrayList<>();
            int failCount = 0;

            // Calculate track division count
            int trackDivisions = trackLength / TRACK_DIVISOR;
            for (int i=0; i<trackDivisions; i++) {
                int selection = i*(trackLength / trackDivisions) + r.nextInt(trackLength / trackDivisions);
                TrackPiece selected = pieces.get(selection);
                if (selected.getType().isCorner() || previousChoices.contains(selection)) {
                    i--;
                    failCount++;
                    if (failCount > 500) {
                        break;
                    }
                    continue;
                }
                previousChoices.add(selection);
                var locationLine = getLineFromPiece(pieces.get(selection));
                for (var location : locationLine) {
                    spaces.add(new PowerupSpace(location.getFirst(), location.getSecond(), this, randomisedPowerups.poll()));
                }

            }
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating class. This should not happen");
        }

    }

    private List<Pair<Double, Double>> getLineFromPiece(TrackPiece piece) {
        switch (piece.getType()) {
            case DOWN:
            case UP:
                return List.of(
                        new Pair<>(piece.getX() - piece.getScale() / 4, piece.getY()),
                        new Pair<>(piece.getX(), piece.getY()),
                        new Pair<>(piece.getX() + piece.getScale() / 4, piece.getY())
                );
            case LEFT:
            case RIGHT:
                return List.of(
                        new Pair<>(piece.getX(), piece.getY() - piece.getScale() / 4),
                        new Pair<>(piece.getX(), piece.getY()),
                        new Pair<>(piece.getX(), piece.getY() + piece.getScale() / 4)
                );
            default:
                throw new RuntimeException("Piece is not a straight");
        }
    }

    public Powerup getNext() {
        return randomisedPowerups.poll();
    }

    public void pickedUp(Powerup powerup) {
        randomisedPowerups.add(powerup);
    }

    public void update() {
        for (int i=0; i<spaces.size(); i++) {
            spaces.get(i).update();
        }
    }

    @Override
    public void draw() {
        for (int i=0; i<spaces.size(); i++) {
            spaces.get(i).draw();
        }
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    public List<PowerupSpace> getSpaces() {
        return spaces;
    }
}
