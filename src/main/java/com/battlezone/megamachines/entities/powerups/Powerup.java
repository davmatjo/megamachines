package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.renderer.*;

public abstract class Powerup {

    public abstract Texture getTexture();

    protected abstract void pickup(RWDCar pickup);

    public abstract void activate(RWDCar activated);

}
