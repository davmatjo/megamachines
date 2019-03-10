package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
import com.battlezone.megamachines.world.track.generator.TrackLoopMutation2;
import com.battlezone.megamachines.world.track.generator.TrackSquareLoop;
import org.junit.Assert;
import org.junit.Test;

import static com.battlezone.megamachines.world.track.TrackType.*;

public class TrackValidityTest {

    @Test
    public void varyingLengthsTest() {
        final TrackType[][] grid = {{RIGHT, RIGHT}, {RIGHT_DOWN}};
        Assert.assertFalse(Track.isValidTrack(grid));
    }

    @Test
    public void adjacentTrackTest() {
        final TrackType[][] grid1 = {
                {LEFT_UP, UP_RIGHT},
                {LEFT, RIGHT},
                {DOWN_LEFT, RIGHT_DOWN}
        }, grid2 = {
                {LEFT_UP, UP, UP_RIGHT, null},
                {LEFT, null, RIGHT, RIGHT},
                {DOWN_LEFT, DOWN, RIGHT_DOWN, null}
        }, grid3 = {
                {LEFT_UP, UP, UP, UP, UP_RIGHT},
                {LEFT, null, DOWN_RIGHT, DOWN, RIGHT_DOWN},
                {LEFT, null, RIGHT, null, null},
                {LEFT, null, RIGHT, null, null},
                {DOWN_LEFT, DOWN, RIGHT_DOWN, null, null}
        };
        Assert.assertFalse(Track.isValidTrack(grid1));
        Assert.assertFalse(Track.isValidTrack(grid2));
        Assert.assertFalse(Track.isValidTrack(grid3));
    }

    @Test
    public void isLoopTest() {
        final TrackType[][] grid = {
                {LEFT_UP, UP, UP_RIGHT},
                {LEFT, null, RIGHT},
                {DOWN_LEFT, DOWN, RIGHT_DOWN}
        };
        Assert.assertTrue(Track.isValidTrack(grid));
    }

    @Test
    public void nonLoopTest() {
        final TrackType[][] grid = {
                {LEFT_UP, UP, UP},
                {LEFT, null, RIGHT},
                {DOWN_LEFT, DOWN, RIGHT_DOWN}
        };
        Assert.assertFalse(Track.isValidTrack(grid));
    }

    @Test
    public void noFloatingPiecesTest() {
        final TrackType[][] grid = {
                {LEFT_UP, UP, UP, null, LEFT},
                {LEFT, null, RIGHT, null, null},
                {DOWN_LEFT, DOWN, RIGHT_DOWN, null, null}
        };
        Assert.assertFalse(Track.isValidTrack(grid));
    }

    @Test
    public void circleClockwiseValidTest() {
        for (int i = 10; i <= 1000; i += 10) {
            final Track track = new TrackCircleLoop(i, i, true).generateTrack();
            final TrackType[][] grid = track.getGrid();
            Assert.assertTrue(Track.isValidTrack(grid));
        }
    }

    @Test
    public void circleAnticlockwiseValidTest() {
        for (int i = 10; i <= 1000; i += 10) {
            final Track track = new TrackCircleLoop(i, i, false).generateTrack();
            final TrackType[][] grid = track.getGrid();
            Assert.assertTrue(Track.isValidTrack(grid));
        }
    }

    @Test
    public void squareClockwiseValidTest() {
        for (int i = 10; i <= 1000; i += 10) {
            final Track track = new TrackSquareLoop(10, 10, true).generateTrack();
            final TrackType[][] grid = track.getGrid();
            Assert.assertTrue(Track.isValidTrack(grid));
        }
    }

    @Test
    public void squareAnticlockwiseValidTest() {
        for (int i = 10; i <= 1000; i += 10) {
            final Track track = new TrackSquareLoop(1000, 1000, false).generateTrack();
            final TrackType[][] grid = track.getGrid();
            Assert.assertTrue(Track.isValidTrack(grid));
        }
    }

    @Test
    public void loopMutationValidTest() {
        for (int i = 0; i < 2000; i++) {
            final TrackType[][] grid = new TrackLoopMutation2(20, 20).generateTrack().getGrid();
            Assert.assertTrue(Track.isValidTrack(grid));
        }
    }

}
