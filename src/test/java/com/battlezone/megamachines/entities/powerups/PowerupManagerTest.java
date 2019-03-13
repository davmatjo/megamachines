package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.physics.PhysicsEngine;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PowerupManagerTest {

    @Test
    public void getNext() {
        PowerupManager manager = new PowerupManager(new TrackLoopMutation(10, 10).generateTrack(), new PhysicsEngine(), null);
        for (int i = 0; i < 1000; i++) {
            Powerup powerup = manager.getNext();
            assertNotNull(powerup);
            manager.pickedUp(powerup);
        }
    }

    @Test
    public void trackUsesAvailableStraights() {
        for (int i = 10; i < 100; i++) {
            Track track = new TrackCircleLoop(i, i, true).generateTrack();
            int straightCount = 0;
            for (var piece : track.getPieces()) {
                if (piece.getType().isStraight()) {
                    straightCount++;
                }
            }

            PowerupManager manager = new PowerupManager(track, new PhysicsEngine(), null);

            assertTrue(manager.getSpaces().size() <= straightCount);

            var spaces = manager.getSpaces();
            if (spaces.size() / 3 >= track.getPieces().size() / PowerupManager.TRACK_DIVISOR) {
                assertEquals(track.getPieces().size() / PowerupManager.TRACK_DIVISOR, spaces.size() / 3);
            }
        }
    }

    @Test
    public void getNextIsNeverNull() {
        PowerupManager manager = new PowerupManager(
                new TrackSquareLoop(10, 10, true).generateTrack(),
                mock(PhysicsEngine.class),
                mock(Renderer.class)
        );
        for (int i=0; i<100000; i++) {
            var powerup = manager.getNext();
            assertNotNull(powerup);
            manager.pickedUp(powerup);
        }
        var car = mock(RWDCar.class);
        for (int i=0; i<1000; i++) {
            var powerup = manager.getNext();
            manager.pickedUp(powerup.getClass(), car);
            assertNotNull(powerup);
            manager.pickedUp(powerup);

        }
    }

    @Test
    public void update() {

    }
}