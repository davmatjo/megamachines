package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When this powerup is activated, the car will spill oil on the track
 * Cars which are on top of an oil patch have little friction with the road
 */
public class OilSpill extends Powerup {

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
