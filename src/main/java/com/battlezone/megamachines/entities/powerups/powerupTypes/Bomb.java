package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will place a bomb at the current position of the car.
 * Once placed, the bomb will explode in 3 seconds.
 * When the bomb explodes, it will shoot a number of projectiles in all directions
 * Projectiles will have mass, so when they hit a car, they will push it according to our collision calculations
 */
public class Bomb extends Powerup {

    public Bomb(PowerupManager manager, Renderer renderer) {
        super(0, manager, renderer);
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

    }

    @Override
    protected void powerupEnd() {

    }


}