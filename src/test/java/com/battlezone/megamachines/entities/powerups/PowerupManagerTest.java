package com.battlezone.megamachines.entities.powerups;

import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;
import org.junit.Test;

import static org.junit.Assert.*;

public class PowerupManagerTest {

    @Test
    public void getNext() {
        PowerupManager manager = new PowerupManager(new TrackLoopMutation2(10, 10).generateTrack(), null, null);
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

            PowerupManager manager = new PowerupManager(track, null, null);

            assertTrue(manager.getSpaces().size() <= straightCount);

            var spaces = manager.getSpaces();
            if (spaces.size() / 3 >= track.getPieces().size() / PowerupManager.TRACK_DIVISOR) {
                assertEquals(track.getPieces().size() / PowerupManager.TRACK_DIVISOR, spaces.size() / 3);
            }
        }
    }
}