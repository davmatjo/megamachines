package com.battlezone.megamachines.entities.powerups.powerupTypes;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.Powerup;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.PowerupSpace;
import com.battlezone.megamachines.physics.Collidable;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.util.Pair;

public class FakeDrop extends PowerupSpace implements Drawable, Collidable {
    FakeDrop(double x, double y, PowerupManager manager, Powerup initial) {
        super(x, y, manager, initial);
    }

    @Override
    public void collided(double xp, double yp, Collidable c2, Pair<Double, Double> n, double l) {
        if (c2 instanceof RWDCar) {
            c2.collided(xp, yp, this, n, l);
        }
    }
}
