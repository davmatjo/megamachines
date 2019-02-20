package com.battlezone.megamachines.ai;

import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.LinkedList;
import java.util.Queue;

public class TrackRoute {

    private final Queue<Pair<Float, Float>> markers = new LinkedList<>();

    public TrackRoute(Track track) {
        generateRoute(track);
    }

    private void generateRoute(Track track) {
        markers.add(new Pair<>(track.getPieces().get(0).getXf(), track.getPieces().get(0).getYf()));
        for (var trackPiece : track.getPieces()) {
            if (trackPiece.getType().equals(TrackType.UP_RIGHT) ||
                    trackPiece.getType().equals(TrackType.UP_LEFT) ||
                    trackPiece.getType().equals(TrackType.DOWN_RIGHT) ||
                    trackPiece.getType().equals(TrackType.DOWN_LEFT) ||
                    trackPiece.getType().equals(TrackType.LEFT_UP) ||
                    trackPiece.getType().equals(TrackType.LEFT_DOWN) ||
                    trackPiece.getType().equals(TrackType.RIGHT_UP) ||
                    trackPiece.getType().equals(TrackType.RIGHT_DOWN)) {
                markers.add(new Pair<>(trackPiece.getXf(), trackPiece.getYf()));
            }
        }
    }

    public Queue<Pair<Float, Float>> getMarkers() {
        return new LinkedList<>(markers);
    }
}

// UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT,
//    LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN