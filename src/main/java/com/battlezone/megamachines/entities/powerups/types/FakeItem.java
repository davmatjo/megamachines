package com.battlezone.megamachines.entities.powerups.types;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.types.physical.FakeDrop;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

/**
 * When activated, this powerup will place a fake item on the map directly behind the car.
 * The fake item is actually a solid unmovable body which once hit, disappears
 */
public class FakeItem extends Powerup {
    public static final byte id = 3;
    private static final Texture texture = AssetManager.loadTexture("/powerups/fakedrop.png");
    private FakeDrop fd;
    private boolean started = false;

    public FakeItem(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(10, manager, pe, renderer);
    }

    @Override
    public Texture getTexture() {
        return texture;
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

    @Override
    public byte getID() {
        return id;
    }
}
