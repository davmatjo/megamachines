package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;

import java.awt.*;

public class TrackOption extends ListItem {

    private Track track;
    private String newName;

    public TrackOption(String name, TrackGenerator generator) {
        this(name, generator.generateTrack());
    }

    public TrackOption(String name, Track track) {
        super(name, AssetManager.loadTexture(track.generateMinimap(Color.GRAY, Color.GRAY)));
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
