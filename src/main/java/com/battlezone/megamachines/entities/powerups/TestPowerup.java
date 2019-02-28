package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.Texture;

public class TestPowerup extends Powerup {

    @Override
    public Texture getTexture() {
        return Texture.CIRCLE;
    }

    @Override
    public void pickup(RWDCar activated) {
        System.out.println("Powerup!");
    }

    @Override
    public void activate(RWDCar activated) {
        System.out.println("Activate!");
    }
}
