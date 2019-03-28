package com.battlezone.megamachines.physics;


import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.cars.components.RearDifferential;
import com.battlezone.megamachines.entities.cars.components.RegularWheel;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.ScaleController;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * The differential tests
 */
public class DifferentialTests {
    @Test
    public void differentialWorking() throws InterruptedException {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0, "");
        pe.addCar(at);

        at.setSpeed(100);
        at.getFlWheel().setAngularVelocity(100 / at.getFlWheel().getDiameter() / 2);
        at.getFrWheel().setAngularVelocity(100 / at.getFrWheel().getDiameter() / 2);
        at.getBlWheel().setAngularVelocity(100 / at.getBlWheel().getDiameter() / 2);
        at.getBrWheel().setAngularVelocity(100 / at.getBrWheel().getDiameter() / 2);
        at.getEngine().setRPM(at.getGearbox().getNewRPM());
        for (int i = 0; i < 60; i++) {
            pe.crank(1.0 / 60);
        }

        //Comparing the  differential output to the correct physics formula
        Assert.assertEquals((float) (60 * Math.min(at.getBlWheel().getAngularVelocity(), at.getBrWheel().getAngularVelocity()) / (2 * Math.PI)) * at.getBackDifferential().finalDriveRatio, (float) at.getBackDifferential().getNewRPM(), 0.1);

        double oldSpeed1 = at.getBlWheel().getAngularVelocity();
        double oldSpeed2 = at.getBrWheel().getAngularVelocity();

        at.getBackDifferential().sendTorque(100, 1);
        at.getBlWheel().physicsStep(1);
        at.getBrWheel().physicsStep(1);

        double newSpeed1 = at.getBlWheel().getAngularVelocity();
        double newSpeed2 = at.getBrWheel().getAngularVelocity();

        //the differential sends torque to the wheels
        Assert.assertTrue(newSpeed1 > oldSpeed1);
        Assert.assertTrue(newSpeed2 > oldSpeed2);

        //Each wheel should spin at around 50
        RegularWheel lw = new RegularWheel(mock(RWDCar.class));
        RegularWheel rw = new RegularWheel(mock(RWDCar.class));
        RearDifferential d = new RearDifferential(lw, rw);
        d.sendTorque(100, 1);
        Assert.assertEquals(50, lw.getAngularVelocity(), 1);
        Assert.assertEquals(50, rw.getAngularVelocity(), 1);
        Assert.assertEquals(lw.getAngularVelocity(), rw.getAngularVelocity(), 1);
    }

}
