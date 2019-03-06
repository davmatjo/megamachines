package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will make the car more agile by increasing the amount of friction between the wheels and the road
 */
public class Agility extends Powerup {

    public static final int id = 1;
    /**
     * The constructor
     * @param manager The powerup manager this powerup belongs to
     */
    public Agility(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(10, manager, pe, renderer);
    }

    @Override
    public Texture getTexture() {
        return Texture.BLANK;
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

    @Override
    public byte getID() {
        return id;
    }
}
