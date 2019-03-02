package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When activated, this powerup will make the car physically bigger on the screen.
 * When other cars collide with it, they will be deflected more than usual
 */
public class GrowthPowerup extends Powerup {

    public GrowthPowerup(PowerupManager manager) {
        super(200, manager);
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
        holder.setScale(elapsed);
    }

    @Override
    protected void powerupEnd() {
        holder.setScale(1.25f);
    }
}
