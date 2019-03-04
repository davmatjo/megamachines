package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will make the car physically bigger on the screen.
 * When other cars collide with it, they will be deflected more than usual
 */
public class GrowthPowerup extends Powerup {

    /**
     * The constructor
     * @param manager The powerup manager this powerup belongs to
     */
    public GrowthPowerup(PowerupManager manager, Renderer renderer) {
        super(10, manager, renderer);
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

    }

    @Override
    protected void powerupUpdate(double interval) {
        while (holder.getScale() < 3.0) {
            holder.setScale(holder.getScale() + (float)interval);
        }
    }

    @Override
    protected void powerupEnd() {
        holder.setScale(1.25f);
    }
}
