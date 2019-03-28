package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.world.track.TrackType;

public class TrackFromGridGenerator extends TrackGenerator {

    private TrackType[][] trackTypeGrid;

    /**
     * Creates a track from a given track type grid.
     *
     * @param trackTypeGrid The track from the given grid.
     */
    public TrackFromGridGenerator(TrackType[][] trackTypeGrid) {
        super(trackTypeGrid.length, trackTypeGrid[0].length);
        this.trackTypeGrid = trackTypeGrid;
    }

    /**
     * Generates the map.
     */
    @Override
    void generateMap() {
        this.grid = trackTypeGrid;
    }
}
