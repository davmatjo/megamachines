package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When activated, this powerup will make the car physically bigger on the screen.
 * When other cars collide with it, they will be deflected more than usual
 */
public class GrowthPowerup extends Powerup {
    GrowthPowerup(double x, double y) {
        super(x, y);
    }

    @Override
    public Texture getTexture() {
        return null;
    }

    @Override
    protected void pickup(RWDCar pickup) {

    }

    @Override
    public void activate(RWDCar activated) {

    }
}
