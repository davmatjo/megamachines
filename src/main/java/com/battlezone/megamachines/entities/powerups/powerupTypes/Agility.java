package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.renderer.Texture;

/**
 * When activated, this powerup will make the car more agile by increasing the amount of friction between the wheels and the road
 */
public class Agility extends Powerup {
    public Agility(double x, double y, PowerupManager manager) {
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
