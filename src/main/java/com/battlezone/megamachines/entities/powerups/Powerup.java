package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;

public abstract class Powerup {

    public static final Texture CRATE = AssetManager.loadTexture("/powerups/crate.png");
    private final PowerupManager manager;
    protected RWDCar holder;
    protected final double duration;
    protected int elapsed;

    protected Powerup(double duration, PowerupManager manager) {
        this.duration = duration;
        this.manager = manager;
    }

    public void pickup(RWDCar pickup) {
        this.holder = pickup;
        powerupPickup();
    }

    public void activate() {
        manager.powerupActivated(this);
        holder.setCurrentPowerup(null);
        powerupActivate();
    }

    public void update(double interval) {
        elapsed+= interval;
        powerupUpdate(interval);
    }

    public void end() {
        powerupEnd();
        holder = null;
    }

    public boolean isAlive() {
        return elapsed < duration;
    }

    public abstract Texture getTexture();

    protected abstract void powerupPickup();

    protected abstract void powerupActivate();

    protected abstract void powerupUpdate(double interval);

    protected abstract void powerupEnd();
}
