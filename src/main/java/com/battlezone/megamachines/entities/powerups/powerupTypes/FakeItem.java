package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When activated, this powerup will place a fake item on the map directly behind the car.
 * The fake item is actually a solid unmovable body which once hit, disappears
 */
public class FakeItem extends Powerup {

    public FakeItem(PowerupManager manager) {
        super(0, manager);
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
    protected void powerupUpdate() {

    }

    @Override
    protected void powerupEnd() {

    }
}
