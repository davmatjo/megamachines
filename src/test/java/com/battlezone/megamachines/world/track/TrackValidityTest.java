package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.track.generator.TrackCircleLoop;
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
    public void circleValidTest() {
        final Track track = new TrackCircleLoop(10, 10, false).generateTrack();
        final TrackType[][] grid = track.getGrid();
        Assert.assertTrue(Track.isValidTrack(grid));
    }

}
