package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When this powerup is activated, the car will spill oil on the track
 * Cars which are on top of an oil patch have little friction with the road
 */
public class OilSpill extends Powerup {

    public static final int id = 6;
    public OilSpill(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(0, manager, pe, renderer);
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

    }

    @Override
    protected void powerupUpdate(double interval) {

    }

    @Override
    protected void powerupEnd() {

    }

    @Override
    public byte getID() {
        return id;
    }


}
