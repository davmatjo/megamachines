package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class EngineTests {
    @Test
    public void engineWorks() throws InterruptedException {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(at);

        //RPM is set to minimum when engine gets told to move to a too low RPM
        at.getEngine().setRPM(0);
        Assert.assertEquals(at.getEngine().minRPM, at.getEngine().getRPM(), 0);

        //RPM is set properly when inside engine power curve
        at.getEngine().setRPM(4000);
        Assert.assertEquals(4000, at.getEngine().getRPM(), 0);

        //Engine can go past maximum RPM (because the delimitation is the point where the gearbox will change gear,
        //but the engine can still theoretically go beyond this point)(It won't happen in practice)
        at.getEngine().setRPM(200000);
        Assert.assertTrue(at.getEngine().delimitation < at.getEngine().getRPM());

        //Correct amount of torque inside power band
        at.getEngine().setRPM(3000);
        Assert.assertEquals(at.getEngine().getMaxTorque(at.getEngine().delimitation - 1), at.getEngine().getMaxTorque(at.getEngine().getRPM()), 0);

        //Engine loses power after delimitation (where the gearbox should change gear)
        at.getEngine().setRPM(20000);
        Assert.assertTrue(at.getEngine().getMaxTorque(at.getEngine().delimitation - 1) > at.getEngine().getMaxTorque(at.getEngine().getRPM()));
    }
}
