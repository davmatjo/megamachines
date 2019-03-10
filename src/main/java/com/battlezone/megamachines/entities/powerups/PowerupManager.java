package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.powerupTypes.*;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PowerupManager implements Drawable {

    /**
     * The number of tracks is divided by this to work out the number of sections eligible for powerups
     */
    static final int TRACK_DIVISOR = 16;

    /**
     * All possible powerups
     */
    private static final List<Class<? extends Powerup>> POWERUPS = List.of(GrowthPowerup.class, Agility.class, FakeItem.class, OilSpill.class, Bomb.class);

    /**
     * Number of random powerups to store
     */
    private static final int POWERUP_BUFFER_SIZE = 100;

    /**
     * The model of each PowerupSpace
     *
     * @see PowerupSpace
     */
    private static final Model model = Model.SQUARE;

    /**
     * The randomised powerups
     */
    private final Queue<Class<? extends Powerup>> randomisedPowerups;

    private final Map<Class<? extends Powerup>, Queue<Powerup>> buffer = new HashMap<>();

    /**
     * All the powerup spaces assigned
     */
    private final List<PowerupSpace> spaces;

    /**
     * Powerups that are currently activated
     */
    private final List<Powerup> activePowerups;

    private final List<Pair<Double, Double>> locationLines;

    private final PhysicsEngine physicsEngine;

    /**
     * Creates a new PowerupManager based on a given track
     *
     * @param track         Track to base this class on
     * @param physicsEngine Physics engine to pass to the created powerups
     * @param renderer      Renderer to pass to the created powerups
     */
    public PowerupManager(Track track, PhysicsEngine physicsEngine, Renderer renderer) {
//        try {
        Random r = new Random();
        this.spaces = new ArrayList<>();
        this.activePowerups = new ArrayList<>();
        this.randomisedPowerups = new LinkedList<>();
        this.physicsEngine = physicsEngine;

        List<TrackPiece> pieces = track.getPieces();
        int trackLength = pieces.size();

        // Fill the powerup buffer with random powerups
        for (int i = 0; i < POWERUP_BUFFER_SIZE; i++) {
            int selection = r.nextInt(POWERUPS.size());
            Class<? extends Powerup> powerup = POWERUPS.get(selection);
//                randomisedPowerups.add(powerup.getDeclaredConstructor(PowerupManager.class, PhysicsEngine.class, Renderer.class).newInstance(this, physicsEngine, renderer));
            randomisedPowerups.add(powerup);
        }
        try {
            var that = this;
            for (var powerupType : POWERUPS) {
                buffer.put(powerupType, new LinkedList<>() {{
                    for (int i = 0; i < 50; i++) {
                        add(powerupType.getDeclaredConstructor(PowerupManager.class, PhysicsEngine.class, Renderer.class).newInstance(that, physicsEngine, renderer));
                    }
                }});
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("this should not happen");
        }

        final List<Integer> previousChoices = new ArrayList<>();
        int failCount = 0;

        // Calculate track division count
        int trackDivisions = trackLength / TRACK_DIVISOR;
        locationLines = new ArrayList<>();
        for (int i = 0; i < trackDivisions; i++) {
            int selection = i * (trackLength / trackDivisions) + r.nextInt(trackLength / trackDivisions);
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
            locationLines.addAll(locationLine);
        }
        System.out.println(locationLines);
//        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error creating class. This should not happen");
//        }

    }

    private PowerupManager(List<Pair<Double, Double>> locations, PhysicsEngine pe, Renderer renderer) {
        randomisedPowerups = new LinkedList<>();
        spaces = new ArrayList<>();
        activePowerups = new ArrayList<>();
        locationLines = locations;
        physicsEngine = pe;

        try {
            var that = this;
            for (var powerupType : POWERUPS) {
                buffer.put(powerupType, new LinkedList<>() {{
                    for (int i = 0; i < 50; i++) {
                        add(powerupType.getDeclaredConstructor(PowerupManager.class, PhysicsEngine.class, Renderer.class).newInstance(that, physicsEngine, renderer));
                    }
                }});
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("this should not happen");
        }

        initSpaces();
    }

    public void initSpaces() {
        System.out.println("spaces" + locationLines);
        for (var location : locationLines) {
            PowerupSpace space = new PowerupSpace(location.getFirst(), location.getSecond(), this, buffer.getOrDefault(randomisedPowerups.poll(), new LinkedList<>()).poll());
            spaces.add(space);
            physicsEngine.addCollidable(space);
        }
    }

    /**
     * Works out a line of powerup positions from a given track piece
     *
     * @param piece The piece to base the positions off
     * @return A line of 3 powerups with an orientation dependent on the piece direction
     */
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

    /**
     * @return The next powerup
     */
    Powerup getNext() {
        if (randomisedPowerups.peek() != null) {
            var type = randomisedPowerups.poll();
            randomisedPowerups.add(type);
            return buffer.get(type).poll();
        }
        return null;
    }

    /**
     * Inform the manager that a powerup was picked up, returns it to the queue of powerups
     *
     * @param powerup the powerup that was picked up
     */
    void pickedUp(Powerup powerup) {
        buffer.get(powerup.getClass()).add(powerup);
    }

    public void pickedUp(Class<? extends Powerup> p, RWDCar toPickup) {
        if (toPickup.getCurrentPowerup() == null || !toPickup.getCurrentPowerup().getClass().equals(p))
            if (p != null) {
                var powerup = buffer.get(p).poll();
                powerup.pickup(toPickup);
                toPickup.setCurrentPowerup(powerup);
                buffer.get(p).add(powerup);
            } else {
                toPickup.setCurrentPowerup(null);
            }
    }

    /**
     * Updates all the powerups for this frame
     *
     * @param interval The time in seconds since this was last called
     */
    public void update(double interval) {
        for (int i = 0; i < spaces.size(); i++) {
            spaces.get(i).update();
        }
        for (int i = 0; i < activePowerups.size(); i++) {
            Powerup p = activePowerups.get(i);
            p.update(interval);
            if (!p.isAlive()) {
                activePowerups.remove(p);
                p.end();
                i--;
            }
        }
    }

    /**
     * Inform the manager that a powerup has been used
     *
     * @param p The powerup that was used
     */
    void powerupActivated(Powerup p) {
        activePowerups.add(p);
    }

    /**
     * Draws all powerup spaces to the screen
     */
    @Override
    public void draw() {
        for (int i = 0; i < spaces.size(); i++) {
            spaces.get(i).draw();
        }
    }

    /**
     * @return The model of all the powerup spaces
     */
    @Override
    public Model getModel() {
        return model;
    }

    /**
     * @return The shader for all the powerup spaces
     */
    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    /**
     * @return The depth to render all powerup spaces at
     */
    @Override
    public int getDepth() {
        return 0;
    }

    /**
     * @return All the powerup spaces
     */
    List<PowerupSpace> getSpaces() {
        return spaces;
    }

    /**
     * Converts critical manager information to a byte array to send to the network
     *
     * @return A representation of this as a byte array
     */
    public byte[] toByteArray() {
        try {
            var bytes = new ByteArrayOutputStream();
            var out = new ObjectOutputStream(bytes);
            System.out.println(bytes.size());
            for (var location : locationLines) {
                out.writeObject(location);
            }
            System.out.println(bytes.size());
            return bytes.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Uses the byte array form of this class to create a new instance of this manager
     *
     * @param b byte array representation of a PowerupManager
     * @return A new powerup manager from b
     */
    public static PowerupManager fromByteArray(byte[] b, PhysicsEngine pe, Renderer r) {
        try {
            var bytes = new ByteArrayInputStream(b);
            var in = new ObjectInputStream(bytes);

            List<Pair<Double, Double>> locations = new ArrayList<>();
            while (bytes.available() > 0) {
                var obj = in.readObject();
                if (obj instanceof Pair) {
                    var pos = (Pair<Double, Double>) obj;
                    locations.add(pos);
                } else {
                    throw new RuntimeException("Got unexpected object");
                }
            }
            return new PowerupManager(locations, pe, r);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
