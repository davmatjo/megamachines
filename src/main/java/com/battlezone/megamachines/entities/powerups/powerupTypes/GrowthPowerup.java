package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

/**
 * When activated, this powerup will make the car physically bigger on the screen.
 * When other cars collide with it, they will be deflected more than usual
 */
public class GrowthPowerup extends Powerup {

    public static final int id = 5;
    private static final Texture texture = AssetManager.loadTexture("/powerups/grow.png");

    /**
     * The constructor
     *
     * @param manager The powerup manager this powerup belongs to
     */
    public GrowthPowerup(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
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
        holder.growthActivated();
        holder.setDepth(1);
    }

    @Override
    protected void powerupUpdate(double interval) {
        if (holder.getScale() < 3.0) {
            holder.setScale(holder.getScale() + (float) interval * 2);
        }
    }

    @Override
    protected void powerupEnd() {
        holder.setScale(1.25f);
        holder.setDepth(0);
        holder.growthDeactivated();
    }
}
