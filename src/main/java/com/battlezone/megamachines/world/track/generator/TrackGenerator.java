package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector2f;
import com.battlezone.megamachines.networking.NewServer;
import com.battlezone.megamachines.util.Utils;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackEdges;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.ArrayList;
import java.util.List;

import static com.battlezone.megamachines.world.track.TrackType.*;

public abstract class TrackGenerator {

    List<TrackPiece> pieces;
    TrackType[][] grid;
    TrackPiece[][] pieceGrid;
    final int tracksAcross, tracksDown;
    int startPieceX, startPieceY;
    List<TrackEdges> edges;
    List<Vector2f> startGrid;
    private final static float OFFSET = ScaleController.TRACK_SCALE / 6;

    public TrackGenerator(int tracksAcross, int tracksDown) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;

        grid = new TrackType[tracksAcross][tracksDown];
        pieceGrid = new TrackPiece[tracksAcross][tracksDown];
        pieces = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public static List<Vector2f> calculateStartingPositions(TrackPiece startPiece, List<TrackPiece> pieces) {
        List<Vector2f> startPositions = new ArrayList<>();
        boolean leftHeavy = true;
        int index = pieces.indexOf(startPiece);
        TrackPiece piece = startPiece;
        int count = 0;
        int reqCount = NewServer.MAX_PLAYERS; //(int) Math.ceil(NewServer.MAX_PLAYERS / 3d);
        while (count < reqCount) {
            final float x = piece.getXf(), y = piece.getYf();
            switch (piece.getType()) {
                case UP:
                    if (leftHeavy) {
                        startPositions.add(topLeft(x, y));
                        startPositions.add(middleRight(x, y));
                        startPositions.add(bottomLeft(x, y));
                    } else {
                        startPositions.add(topRight(x, y));
                        startPositions.add(middleLeft(x, y));
                        startPositions.add(bottomRight(x, y));
                    }
                    count += 3;
                    break;
                case DOWN:
                    if (leftHeavy) {
                        startPositions.add(bottomRight(x, y));
                        startPositions.add(middleLeft(x, y));
                        startPositions.add(topRight(x, y));
                    } else {
                        startPositions.add(bottomLeft(x, y));
                        startPositions.add(middleRight(x, y));
                        startPositions.add(topLeft(x, y));
                    }
                    count += 3;
                    break;
                case LEFT:
                    if (leftHeavy) {
                        startPositions.add(bottomLeft(x, y));
                        startPositions.add(topMiddle(x, y));
                        startPositions.add(bottomRight(x, y));
                    } else {
                        startPositions.add(topLeft(x, y));
                        startPositions.add(bottomMiddle(x, y));
                        startPositions.add(topRight(x, y));
                    }
                    count += 3;
                    break;
                case RIGHT:
                    if (leftHeavy) {
                        startPositions.add(topRight(x, y));
                        startPositions.add(bottomMiddle(x, y));
                        startPositions.add(topLeft(x, y));
                    } else {
                        startPositions.add(bottomRight(x, y));
                        startPositions.add(topMiddle(x, y));
                        startPositions.add(bottomLeft(x, y));
                    }
                    count += 3;
                    break;
            }
            leftHeavy = !leftHeavy;
            do {
                index = MathUtils.wrap(index - 1, 0, pieces.size());
                piece = pieces.get(index);
            } while (Utils.equalsOr(piece.getType(), UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN));
        }
        return startPositions;
    }

    private static Vector2f topLeft(float x, float y) {
        return new Vector2f(x - OFFSET, y + OFFSET);
    }

    private static Vector2f topMiddle(float x, float y) {
        return new Vector2f(x, y + OFFSET);
    }

    private static Vector2f topRight(float x, float y) {
        return new Vector2f(x + OFFSET, y + OFFSET);
    }

    private static Vector2f middleLeft(float x, float y) {
        return new Vector2f(x - OFFSET, y);
    }

    private static Vector2f middleMiddle(float x, float y) {
        return new Vector2f(x, y);
    }

    private static Vector2f middleRight(float x, float y) {
        return new Vector2f(x + OFFSET, y);
    }

    private static Vector2f bottomLeft(float x, float y) {
        return new Vector2f(x - OFFSET, y - OFFSET);
    }

    private static Vector2f bottomMiddle(float x, float y) {
        return new Vector2f(x, y - OFFSET);
    }

    private static Vector2f bottomRight(float x, float y) {
        return new Vector2f(x + OFFSET, y - OFFSET);
    }

    public Track generateTrack() {
        generateMap();
        typeToPieceGrid(grid, pieceGrid, tracksAcross, tracksDown);
        findStartingPoint();
        populateListInOrder(pieces, edges, pieceGrid, startPieceX, startPieceY);
        startGrid = calculateStartingPositions(pieceGrid[startPieceX][startPieceY], pieces);
        return new Track(pieces, grid, pieceGrid, startPieceX, startPieceY, edges, startGrid);
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
