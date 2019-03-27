package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class WheeledObjectTests {
    @Test
    public void wheeledObjectTests() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0, "");
        pe.addCar(at);

        Assert.assertTrue(at.isFrontWheel(at.getFlWheel()));
        Assert.assertTrue(at.isFrontWheel(at.getFrWheel()));
        Assert.assertFalse(at.isFrontWheel(at.getBlWheel()));
        Assert.assertFalse(at.isFrontWheel(at.getBrWheel()));

        Assert.assertTrue(at.isFrontLeftWheel(at.getFlWheel()));
        Assert.assertFalse(at.isFrontLeftWheel(at.getFrWheel()));
        Assert.assertFalse(at.isFrontLeftWheel(at.getBlWheel()));
        Assert.assertFalse(at.isFrontLeftWheel(at.getBrWheel()));

        Assert.assertFalse(at.isFrontRightWheel(at.getFlWheel()));
        Assert.assertTrue(at.isFrontRightWheel(at.getFrWheel()));
        Assert.assertFalse(at.isFrontRightWheel(at.getBlWheel()));
        Assert.assertFalse(at.isFrontRightWheel(at.getBrWheel()));

        Assert.assertFalse(at.isBackLeftWheel(at.getFlWheel()));
        Assert.assertFalse(at.isBackLeftWheel(at.getFrWheel()));
        Assert.assertTrue(at.isBackLeftWheel(at.getBlWheel()));
        Assert.assertFalse(at.isBackLeftWheel(at.getBrWheel()));

        Assert.assertFalse(at.isBackRightWheel(at.getFlWheel()));
        Assert.assertFalse(at.isBackRightWheel(at.getFrWheel()));
        Assert.assertFalse(at.isBackRightWheel(at.getBlWheel()));
        Assert.assertTrue(at.isBackRightWheel(at.getBrWheel()));

        Assert.assertEquals(1630, at.getWeight(), 0);
        Assert.assertEquals(1.35, at.getDistanceCenterOfWeightRearAxle(), 0);
        Assert.assertEquals(1.65, at.getDistanceCenterOfWeightFrontAxle(), 0);
        Assert.assertEquals(0, at.getLongitudinalWeightTransfer(), 0);
        Assert.assertEquals(0, at.getLateralWeightTransfer(), 0);

        Assert.assertEquals(1.65, at.getDistanceToCenterOfWeightLongitudinally(at.getFlWheel()), 0);
        Assert.assertEquals(1.65, at.getDistanceToCenterOfWeightLongitudinally(at.getFrWheel()), 0);
        Assert.assertEquals(1.35, at.getDistanceToCenterOfWeightLongitudinally(at.getBlWheel()), 0);
        Assert.assertEquals(1.35, at.getDistanceToCenterOfWeightLongitudinally(at.getBrWheel()), 0);

        Assert.assertEquals(448.25, at.getLoadOnWheel(at.getFlWheel(), at.getWeight(), 0, at.wheelBase), 0);
        Assert.assertEquals(896.5, at.getLoadOnWheel(at.getFlWheel(), at.getWeight(), 1, at.wheelBase), 0);
        Assert.assertEquals(448.25, at.getLoadOnWheel(at.getFrWheel(), at.getWeight(), 0, at.wheelBase), 0);
        Assert.assertEquals(896.5, at.getLoadOnWheel(at.getFrWheel(), at.getWeight(), 1, at.wheelBase), 0);

        Assert.assertEquals(366.75, at.getLoadOnWheel(at.getBlWheel(), at.getWeight(), 0, at.wheelBase), 0);
        Assert.assertEquals(733.5, at.getLoadOnWheel(at.getBlWheel(), at.getWeight(), 1, at.wheelBase), 0);
        Assert.assertEquals(366.75, at.getLoadOnWheel(at.getBrWheel(), at.getWeight(), 0, at.wheelBase), 0);
        Assert.assertEquals(733.5, at.getLoadOnWheel(at.getBrWheel(), at.getWeight(), 1, at.wheelBase), 0);
    }
}
