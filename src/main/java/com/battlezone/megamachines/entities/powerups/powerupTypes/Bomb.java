package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When activated, this powerup will place a bomb at the current position of the car.
 * Once placed, the bomb will explode in 3 seconds.
 * When the bomb explodes, it will shoot a number of projectiles in all directions
 * Projectiles will have mass, so when they hit a car, they will push it according to our collision calculations
 */
public class Bomb extends Powerup {
    protected Bomb(double x, double y, PowerupManager manager) {
        super(x, y, manager);
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
