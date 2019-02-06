package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.world.ScaleController;
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
    int startPieceX, startPieceY;
    List<TrackEdges> edges;

    public TrackGenerator(int tracksAcross, int tracksDown) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;

        grid = new TrackType[tracksAcross][tracksDown];
        pieceGrid = new TrackPiece[tracksAcross][tracksDown];
        pieces = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public Track generateTrack() {
        generateMap();
        ArrayUtil.prettyPrint(grid);
        typeToPieceGrid(grid, pieceGrid, tracksAcross, tracksDown);
        findStartingPoint();
        populateListInOrder(pieces, edges, pieceGrid, startPieceX, startPieceY);
        return new Track(pieces, grid, pieceGrid, startPieceX, startPieceY, edges);
    }

    abstract void generateMap();

    public static TrackPiece[][] typeToPieceGrid(TrackType[][] types, TrackPiece[][] pieces, final int tracksAcross, final int tracksDown) {
        for (int x = 0; x < tracksAcross; x++)
            for (int y = 0; y < tracksDown; y++)
                if (types[x][y] != null)
                    pieces[x][y] = new TrackPiece(x * ScaleController.TRACK_SCALE, y * ScaleController.TRACK_SCALE, types[x][y]);
        return pieces;
    }

    public static void populateListInOrder(List<TrackPiece> pieces, List<TrackEdges> edges, TrackPiece[][] grid, final int startX, final int startY) {
        // Start at the beginning
        int tempX = startX, tempY = startY;

        // Create the first piece
        TrackPiece piece = grid[startX][startY];
        pieces.add(piece);
        edges.add(new TrackEdges(piece));

        do {
            // Check the type of the current piece
            switch (grid[tempX][tempY].getType()) {
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
            piece = grid[tempX][tempY];
            pieces.add(piece);
            edges.add(new TrackEdges(piece));
        } while (!(tempX == startX && tempY == startY));
    }

    private void findStartingPoint() {
        // Move upward and to the right
        while (pieceGrid[startPieceX][startPieceY] == null) {
            // Move them in sync
            startPieceX = ++startPieceY;
        }
    }

}
