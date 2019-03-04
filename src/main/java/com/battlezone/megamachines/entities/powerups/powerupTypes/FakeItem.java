package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.Renderer;

/**
 * When activated, this powerup will place a fake item on the map directly behind the car.
 * The fake item is actually a solid unmovable body which once hit, disappears
 */
public class FakeItem extends Powerup {

    public FakeItem(PowerupManager manager, PhysicsEngine pe, Renderer renderer) {
        super(0, manager, pe, renderer);
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
        new FakeDrop(holder.getX(), holder.getY(), physicsEngine, renderer);
    }

    @Override
    protected void powerupUpdate(double interval) {

    }

    @Override
    protected void powerupEnd() {

    }
}
