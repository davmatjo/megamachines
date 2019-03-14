package com.battlezone.megamachines.entities.powerups.types;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.entities.powerups.PowerupManager;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.game.Renderer;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AgilityTest {

    private static Agility agility;
    private static PowerupManager manager = mock(PowerupManager.class);
    private static Renderer renderer = mock(Renderer.class);
    private static PhysicsEngine physicsEngine = mock(PhysicsEngine.class);
    private static RWDCar car = mock(RWDCar.class);

    @BeforeClass
    public static void setup() {
        agility = new Agility(manager, physicsEngine, renderer);
    }

    @Test
    public void activate() {
        agility.pickup(car);
        agility.activate();
        verify(car).agilityActivated();
    }

    @Test
    public void update() {
        double interval = 16.666666666;
        for (double d = 0; d < agility.getDuration(); d += interval) {
            agility.update(interval);
            verify(car, never()).agilityDeactivated();
        }
    }

    @Test
    public void powerupEnd() {
        double interval = 16.666666666;
        agility.update(interval);
        verify(car).agilityActivated();
    }
}