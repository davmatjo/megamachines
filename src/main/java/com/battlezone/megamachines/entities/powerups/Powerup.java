package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;

public abstract class Powerup {

    public static final Texture CRATE = AssetManager.loadTexture("/powerups/crate.png");

    public abstract Texture getTexture();

    protected abstract void pickup(RWDCar pickup);

    public abstract void activate(RWDCar activated);

}
