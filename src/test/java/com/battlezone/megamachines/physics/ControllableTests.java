package com.battlezone.megamachines.physics;

import com.battlezone.megamachines.ai.Driver;
import com.battlezone.megamachines.entities.cars.AffordThoroughbred;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.entities.powerups.types.Agility;
import com.battlezone.megamachines.entities.powerups.types.FakeItem;
import com.battlezone.megamachines.events.keys.KeyEvent;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.world.MultiplayerWorld;
import com.battlezone.megamachines.world.Race;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ControllableTests {
    @Test
    public void controllableWorks() {
        PhysicsEngine pe = new PhysicsEngine();
        AffordThoroughbred at = new AffordThoroughbred(0, 0, ScaleController.RWDCAR_SCALE, 1, new Vector3f(0, 0, 0), 0, 0, "");
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

        at.setDriver(null);

        at.setDriverPress(KeyCode.W);
        Assert.assertEquals(1, at.getAccelerationAmount(), 0);
        at.setDriverPress(KeyCode.S);
        Assert.assertEquals(1, at.getBrakeAmount(), 0);
        at.setDriverPress(KeyCode.A);
        Assert.assertEquals(1, at.getTurnAmount(), 0);
        at.setDriverPress(KeyCode.D);
        Assert.assertEquals(-1, at.getTurnAmount(), 0);

        MultiplayerWorld.setActive(false);
        FakeItem fi = new FakeItem(mock(PowerupManager.class), pe, mock(Renderer.class));
        fi.pickup(at);
        at.setCurrentPowerup(fi);
        at.setDriverPress(KeyCode.SPACE);
        Assert.assertEquals(null, at.getCurrentPowerup());
        at.setCurrentPowerup(null);
        at.setDriverPress(KeyCode.SPACE);
        Assert.assertEquals(null, at.getCurrentPowerup());
        MultiplayerWorld.setActive(true);
        at.setCurrentPowerup(mock(Agility.class));
        at.setDriverPress(KeyCode.SPACE);
        Assert.assertNotEquals(null, at.getCurrentPowerup());

        at.setDriverRelease(KeyCode.W);
        Assert.assertEquals(0, at.getAccelerationAmount(), 0);
        at.setDriverRelease(KeyCode.S);
        Assert.assertEquals(0, at.getBrakeAmount(), 0);
        at.setDriverPress(KeyCode.A);
        at.setDriverRelease(KeyCode.A);
        Assert.assertEquals(0, at.getTurnAmount(), 0);
        at.setDriverPress(KeyCode.D);
        at.setDriverRelease(KeyCode.D);
        Assert.assertEquals(0, at.getTurnAmount(), 0);

        KeyEvent event = new KeyEvent(KeyCode.W, true);
        at.setDriverPressRelease(event);
        Assert.assertEquals(1, at.getAccelerationAmount(), 0);
        KeyEvent event2 = new KeyEvent(KeyCode.W, false);
        at.setDriverPressRelease(event2);
        Assert.assertEquals(0, at.getAccelerationAmount(), 0);
    }
}
