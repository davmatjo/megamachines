package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

public class WheelTests {
    @Test
    public void wheelsWork() throws InterruptedException {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(at);

        //Checking getter and setter
        at.getBlWheel().setAngularVelocity(5);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 5);
        at.getBlWheel().setAngularVelocity(0);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 0);

        //Appllying acceleration works
        at.getBlWheel().applyAcceleration(5, 4);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 20);
        at.getBlWheel().setAngularVelocity(0);

        //Braking works
        at.getBlWheel().setAngularVelocity(20);
        at.getBlWheel().brake(2, 5);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 10);

        //Braking only stops the car, and doesn't reverse it
        at.getBlWheel().setAngularVelocity(5);
        at.getBlWheel().brake(2, 5);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 0);

        //Braking works in reverse
        at.getBlWheel().setAngularVelocity(-20);
        at.getBlWheel().brake(2, 5);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == -10);

        //Braking only stops the car, and doesn't reverse it (from going backwards to forwards)
        at.getBlWheel().setAngularVelocity(-5);
        at.getBlWheel().brake(2, 5);
        Assert.assertTrue(at.getBlWheel().getAngularVelocity() == 0);

        //Setting the wheel performance modifier works
        at.getBlWheel().setWheelPerformanceMultiplier(1);
        Assert.assertTrue(at.getBlWheel().getWheelPerformanceMultiplier() == 1);

        //Setting the wheel side performance modifier works
        at.getBlWheel().setWheelSidePerformanceMultiplier(1);
        Assert.assertTrue(at.getBlWheel().getWheelSidePerformanceMultiplier() == 1);
    }
}
