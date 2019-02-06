package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackEdges;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.ArrayList;
import java.util.List;

public abstract class TrackGenerator {

    List<TrackPiece> pieces;
    TrackType[][] grid;
    TrackPiece[][] pieceGrid;
    final int tracksAcross, tracksDown;
    final int trackSize;
    int startPieceX, startPieceY;
    List<TrackEdges> edges;

    public TrackGenerator(int tracksAcross, int tracksDown, int trackSize) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;
        this.trackSize = trackSize;

        grid = new TrackType[tracksAcross][tracksDown];
        pieceGrid = new TrackPiece[tracksAcross][tracksDown];

        grid = new TrackType[tracksAcross][tracksDown];
    }

    public Track generateTrack() {
        generateMap();
        convertTypeToPieceGrid();
        findStartingPoint();
        populateListInOrder();
        return new Track(pieces, grid, pieceGrid, trackSize, startPieceX, startPieceY, edges);
    }

    abstract void generateMap();

    private void convertTypeToPieceGrid() {
        for (int i = 0; i < tracksAcross; i++)
            for (int j = 0; j < tracksDown; j++)
                if (grid[i][j] != null)
                    pieceGrid[i][j] = new TrackPiece((i) * trackSize, (j) * trackSize, trackSize, grid[i][j]);
    }

    private void populateListInOrder() {
        // Start at the beginning
        int tempX = startPieceX, tempY = startPieceY;

        // Create the first piece
        pieces = new ArrayList<>();
        edges = new ArrayList<>();
        TrackPiece piece = pieceGrid[startPieceX][startPieceY];
        pieces.add(piece);
        edges.add(new TrackEdges(piece));

        do {
            // Check the type of the current piece
            switch (pieceGrid[tempX][tempY].getType()) {
                // Go up
                case UP:
                case LEFT_UP:
                case RIGHT_UP:
                    tempY++;
                    break;
                // Go down
                case DOWN:
                case LEFT_DOWN:
                case RIGHT_DOWN:
                    tempY--;
                    break;
                // Go left
                case LEFT:
                case UP_LEFT:
                case DOWN_LEFT:
                    tempX--;
                    break;
                // Go right
                case RIGHT:
                case UP_RIGHT:
                case DOWN_RIGHT:
                    tempX++;
                    break;
            }
            piece = pieceGrid[tempX][tempY];
            pieces.add(piece);
            edges.add(new TrackEdges(piece));
        } while (!(tempX == startPieceX && tempY == startPieceY));
    }

    private void findStartingPoint() {
        // Move upward and to the right
        while (pieceGrid[startPieceX][startPieceY] == null) {
            // Move them in sync
            startPieceX = ++startPieceY;
        }
    }

}
