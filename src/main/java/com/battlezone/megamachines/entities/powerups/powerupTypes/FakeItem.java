package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will place a fake item on the map directly behind the car.
 * The fake item is actually a solid unmovable body which once hit, disappears
 */
public class FakeItem extends Powerup {
    public static final int id = 4;
    private FakeDrop fd;
    private double elapsed = 0;
    private boolean started = false;

    public FakeItem(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(  10, manager, pe, renderer);
    }

    @Override
    public Texture getTexture() {
        return null;
    }

    @Override
    protected void powerupPickup() {

    }

    @Override
    protected void powerupActivate() {
        fd = new FakeDrop(holder.getX() - ((holder.getScale() + 1.1)) * Math.cos(Math.toRadians(holder.getRotation())), holder.getY() - ((holder.getScale() + 1.1)) * Math.sin(Math.toRadians(holder.getRotation())), physicsEngine, renderer);
    }

    @Override
    protected void powerupUpdate(double interval) {
        if (started) {
            elapsed += interval;
        }
    }

    @Override
    protected void powerupEnd() {
        physicsEngine.removeCollidable(fd);
        renderer.removeDrawable(fd);
        started = false;
        elapsed = 0;
    }
}
