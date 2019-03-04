package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will make the car more agile by increasing the amount of friction between the wheels and the road
 */
public class Agility extends Powerup {

    /**
     * The constructor
     * @param manager The powerup manager this powerup belongs to
     */
    public Agility(PowerupManager manager, Renderer renderer) {
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
        holder.agilityActivated();
    }

    @Override
    protected void powerupUpdate(double interval) {

    }

    @Override
    protected void powerupEnd() {
        holder.agilityDeactivated();
    }
}
