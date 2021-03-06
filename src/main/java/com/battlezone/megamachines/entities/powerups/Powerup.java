package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.types.*;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.AnimatedTexture;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

import java.util.Map;

/**
 * This class represents a generic powerup
 */
public abstract class Powerup {

    public static final Map<Byte, Class<? extends Powerup>> POWERUP_MAP = Map.of(
            Agility.id, Agility.class,
            Bomb.id, Bomb.class,
            FakeItem.id, FakeItem.class,
            Growth.id, Growth.class,
            OilSpill.id, OilSpill.class
    );

    /**
     * Identification for each powerup
     */
    public static final byte id = 0;
    /**
     * The texture of the crate that gets displayed before the powerup gets picked up
     */
    public static Texture CRATE = AssetManager.loadTexture("/powerups/crate.png");
    /**
     * The texture of the oil spill that is displayed on the track when the powerup is used
     */
    public static Texture OIL_SPILL = AssetManager.loadTexture("/powerups/oil_spill.png");
    /**
     * The animated texture of the bomb that is displayed on the track when the powerup is used
     */
    public static AnimatedTexture BOMB = AssetManager.loadAnimation("/powerups/bomb_", 4, 8, true);
    /**
     * The texture of the broken crate that gets displayed when the powerup is picked up
     */
    static Texture BROKEN_CRATE = AssetManager.loadTexture("/powerups/crate_broken.png");
    /**
     * The powerup manager
     */
    protected final PowerupManager manager;

    /**
     * The renderer
     */
    protected final Renderer renderer;

    /**
     * The current physics engine
     */
    protected final PhysicsEngine physicsEngine;
    /**
     * The duration of this powerup
     */
    private final double duration;
    /**
     * The car this powerup belongs to
     */
    protected RWDCar holder;
    /**
     * The time elapsed since the powerup has been activated
     */
    protected double elapsed;

    /**
     * The constructor
     *
     * @param duration This powerup's duration
     * @param manager  The powerup manager this powerup belongs to
     */
    protected Powerup(double duration, PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        this.duration = duration;
        this.manager = manager;
        this.renderer = renderer;
        this.physicsEngine = pe;
    }

    /**
     * This function gets called when a powerup gets picked up
     *
     * @param pickup The car that picks up this powerup
     */
    public void pickup(RWDCar pickup) {
        this.holder = pickup;
        powerupPickup();
    }

    /**
     * This function gets called when the powerup is activated by its owner
     */
    public void activate() {
        manager.powerupActivated(this);
        holder.setCurrentPowerup(null);
        powerupActivate();
    }

    /**
     * This function gets called periodically to update the state of the powerup
     *
     * @param interval The interval since the function has been last called
     */
    public void update(double interval) {
        elapsed += interval;
        powerupUpdate(interval);
    }

    /**
     * Ends the powerup
     */
    public void end() {
        powerupEnd();
        holder = null;
    }

    /**
     * Returns true if the powerup is alive, false otherwise
     *
     * @return true if the powerup is alive, false otherwise
     */
    boolean isAlive() {
        return elapsed < duration;
    }

    /**
     * Gets the texture of this powerup
     *
     * @return The texture of this powerup
     */
    public abstract Texture getTexture();

    /**
     * Gets called when the powerup has been picked up
     */
    protected abstract void powerupPickup();

    /**
     * Gets called when the powerup has been activated
     */
    protected abstract void powerupActivate();

    /**
     * Gets called when the powerup has been updated
     *
     * @param interval The interval since the powerup was last updated
     */
    protected abstract void powerupUpdate(double interval);

    /**
     * Gets called when the powerup ends
     */
    protected abstract void powerupEnd();

    public double getDuration() {
        return duration;
    }

    public abstract byte getID();
}
