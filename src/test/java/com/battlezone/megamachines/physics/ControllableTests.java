package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.powerups.types.Agility;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ControllableTests {
    @Test
    public void correctParts() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0);
        pe.addCar(at);

        at.setAccelerationAmount(1);
        Assert.assertEquals(1,  at.getAccelerationAmount(), 0);
        at.setAccelerationAmount(5);
        Assert.assertEquals(5,  at.getAccelerationAmount(), 0);
        at.setAccelerationAmount(0);
        Assert.assertEquals(0,  at.getAccelerationAmount(), 0);

        at.setBrakeAmount(1);
        Assert.assertEquals(1,  at.getBrakeAmount(), 0);
        at.setBrakeAmount(5);
        Assert.assertEquals(5,  at.getBrakeAmount(),  0);
        at.setBrakeAmount(0);
        Assert.assertEquals(0,  at.getBrakeAmount(), 0);

        at.setTurnAmount(1);
        Assert.assertEquals(1,  at.getTurnAmount(), 0);
        at.setTurnAmount(-5);
        Assert.assertEquals(-5,  at.getTurnAmount(),  0);
        at.setTurnAmount(0);
        Assert.assertEquals(0,  at.getTurnAmount(), 0);

        Assert.assertEquals(null,  at.getCurrentPowerup());
        at.setCurrentPowerup(new Agility(null, null, null));
        Assert.assertEquals(Agility.class,  at.getCurrentPowerup().getClass());

        Assert.assertEquals(null,  at.getDriver());
        at.setDriver(new Driver(mock(Track.class), at, mock(Race.class)));
        Assert.assertEquals(Driver.class,  at.getDriver().getClass());

        Assert.assertEquals(true,  at.isControlsActive());
        at.setControlsActive(false);
        Assert.assertEquals(false,  at.isControlsActive());
        at.setControlsActive(true);
        Assert.assertEquals(true,  at.isControlsActive());
        at.setControlsActive(true);
        Assert.assertEquals(true,  at.isControlsActive());
        at.setControlsActive(false);
        Assert.assertEquals(false,  at.isControlsActive());
    }
}
