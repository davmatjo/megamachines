package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class GearboxTests {
    @Test
    public void gearboxWorks() throws InterruptedException {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(at);

        int oldGear = 0;

        //The gearbox should not change gear when rpm is still fine
        at.getEngine().setRPM(at.getEngine().delimitation - 1);
        oldGear = at.getGearbox().getCurrentGear();
        at.getGearbox().checkShift(at.getEngine());
        Assert.assertEquals(oldGear, at.getGearbox().getCurrentGear());

        //The gearbox should shift up if RPM is too high
        at.getEngine().setRPM(at.getEngine().delimitation + 1);
        oldGear = at.getGearbox().getCurrentGear();
        at.getGearbox().checkShift(at.getEngine());
        Assert.assertEquals(oldGear + 1, at.getGearbox().getCurrentGear());

        //The gearbox should shift down if RPM is too low
        at.getEngine().setRPM(at.getEngine().minRPM - 1);
        oldGear = at.getGearbox().getCurrentGear();
        at.getGearbox().checkShift(at.getEngine());
        Assert.assertEquals(oldGear - 1, at.getGearbox().getCurrentGear());

        //Gearbox should not be in reverse after telling it so
        at.getGearbox().engageReverse(false);
        Assert.assertNotEquals(0, at.getGearbox().getCurrentGear());
        Assert.assertFalse(at.getGearbox().isOnReverse());

        //Gearbox should be in reverse after telling it so
        at.getGearbox().engageReverse(true);
        Assert.assertEquals(0, at.getGearbox().getCurrentGear());
        Assert.assertTrue(at.getGearbox().isOnReverse());

        //Gearbox should be in reverse after telling it so
        at.getGearbox().engageReverse(true);
        Assert.assertEquals(0, at.getGearbox().getCurrentGear());
        Assert.assertTrue(at.getGearbox().isOnReverse());

        //Gearbox should not be in reverse after telling it so
        at.getGearbox().engageReverse(false);
        Assert.assertNotEquals(0, at.getGearbox().getCurrentGear());
        Assert.assertFalse(at.getGearbox().isOnReverse());

        //Correct amount of gearbox losses
        Assert.assertEquals(0.1, at.getGearbox().getGearboxLosses(), 0);

        //Car not moving currently
        Assert.assertEquals(at.getEngine().minRPM, at.getGearbox().getNewRPM(), 100);

        //Gearbox should change gear after a while
        for (int i = 0; i < 200; i++) {
            at.getGearbox().sendTorque(100, 1);
            pe.crank(1);
        }
        Assert.assertNotEquals(at.getGearbox().getCurrentGear(), 1);

        //Gearbox can be in any gear, since this is not a Trabant
        for (int i = 0; i < 6; i++) {
            at.getGearbox().setCurrentGear((byte)i);
            Assert.assertTrue(i == at.getGearbox().getCurrentGear());
        }
    }
}
