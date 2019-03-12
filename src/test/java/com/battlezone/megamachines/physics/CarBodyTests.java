package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.Cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.Cars.BerrariB150;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

/**
 * The tests for car body
 */
public class CarBodyTests {
    @Test
    public void correctWeight() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(at);
        Assert.assertEquals(1200.0, at.getCarBody().getWeight(), 0);

        BerrariB150 bb = new BerrariB150(100, 0, ScaleController.RWDCAR_SCALE, 2, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(bb);
        Assert.assertEquals(800.0, bb.getCarBody().getWeight(), 0);
    }
}
