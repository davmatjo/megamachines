package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.track.generator.TrackLoopMutation;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class TrackStorageManagerTest {

    @Test
    public void saveTrack() {
        //clear
        var manager = new TrackStorageManager();
        Arrays.stream(new File("user_tracks/").listFiles()).forEach(File::delete);

        var track = new TrackLoopMutation(20, 20).generateTrack();
        manager.saveTrack(track);

        var saved = manager.getTrackOptions().get(0);
        Assert.assertArrayEquals(track.getGrid(), saved.getTrack().getGrid());
    }
}