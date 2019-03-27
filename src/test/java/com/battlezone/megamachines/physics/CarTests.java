package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.cars.components.*;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class CarTests {
    @Test
    public void correctParts() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0, "");
        pe.addCar(at);

        Assert.assertEquals(RegularChasis.class, at.getCarBody().getClass());
        Assert.assertEquals(RearDifferential.class, at.getBackDifferential().getClass());
        Assert.assertEquals(RWDDriveShaft.class, at.getDriveShaft().getClass());
        Assert.assertEquals(SmallTurboEngine.class, at.getEngine().getClass());
        Assert.assertEquals(AutomaticSixSpeedGearbox.class, at.getGearbox().getClass());
    }
}
