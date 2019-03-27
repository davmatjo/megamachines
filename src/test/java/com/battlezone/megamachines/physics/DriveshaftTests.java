package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class DriveshaftTests {
    @Test
    public void driveShaftWorks() throws InterruptedException {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0, "");
        pe.addCar(at);

        //Sending torque dirrectly from the differential should spin the wheels
        //As the drive shaft only serves as a kind of interface between the differential and gearbox,
        //No further testing is needed
        at.getDriveShaft().sendTorque(100, 1);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() > 0);
        Assert.assertTrue(at.getBrWheel().getAngularVelocity() > 0);
    }
}