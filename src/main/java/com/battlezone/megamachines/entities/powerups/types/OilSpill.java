package com.battlezone.megamachines.entities.powerups.types;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.types.physical.OilSpillOnGround;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

/**
 * When this powerup is activated, the car will spill oil on the track
 * Cars which are on top of an oil patch have little friction with the road
 */
public class OilSpill extends Powerup {
    public static final byte id = 5;
    private OilSpillOnGround spill;
    private boolean started = false;
    private static final Texture texture = AssetManager.loadTexture("/powerups/oil.png");

    public OilSpill(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(  10, manager, pe, renderer);
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
        spill = new OilSpillOnGround(holder.getX() - ((holder.getScale() + 1.1)) * Math.cos(Math.toRadians(holder.getRotation())), holder.getY() - ((holder.getScale() + 1.1)) * Math.sin(Math.toRadians(holder.getRotation())), physicsEngine, renderer);
    }

    @Override
    protected void powerupUpdate(double interval) {
        if (started) {
            elapsed += interval;
        }
    }

    @Override
    protected void powerupEnd() {
        physicsEngine.removeCollidable(spill);
        renderer.removeDrawable(spill);
        started = false;
        elapsed = 0;
    }

    @Override
    public byte getID() {
        return id;
    }
}
