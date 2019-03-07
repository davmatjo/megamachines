package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.world.track.TrackType;

public class TrackFromGridGenerator extends TrackGenerator {

    private TrackType[][] trackTypeGrid;

    public TrackFromGridGenerator(TrackType[][] trackTypeGrid) {
        super(trackTypeGrid.length, trackTypeGrid[0].length);
        this.trackTypeGrid = trackTypeGrid;
    }

    @Override
    void generateMap() {
        this.grid = trackTypeGrid;
    }
}
