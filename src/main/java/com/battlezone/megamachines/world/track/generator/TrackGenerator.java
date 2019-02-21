package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.util.Pair;
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
    List<Vector3f> startGrid;
    private final static float OFFSET = ScaleController.TRACK_SCALE / 6;

    public TrackGenerator(int tracksAcross, int tracksDown) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;

        grid = new TrackType[tracksAcross][tracksDown];
        pieceGrid = new TrackPiece[tracksAcross][tracksDown];
        pieces = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public static List<Vector3f> calculateStartingPositions(TrackPiece startPiece, List<TrackPiece> pieces) {
        List<Vector3f> startPositions = new ArrayList<>();
        boolean leftHeavy = true;
        int index = pieces.indexOf(startPiece);
        TrackPiece piece = startPiece;
        int count = 0;
        int reqCount = Server.MAX_PLAYERS;
        while (count < reqCount) {
            float x = piece.getXf(), y = piece.getYf();
            final TrackType type = piece.getType();
            switch (type) {
                case UP:
                    if (leftHeavy) {
                        startPositions.add(topLeft(x, y, type));
                        startPositions.add(middleRight(x, y, type));
                        startPositions.add(bottomLeft(x, y, type));
                    } else {
                        startPositions.add(topRight(x, y, type));
                        startPositions.add(middleLeft(x, y, type));
                        startPositions.add(bottomRight(x, y, type));
                    }
                    count += 3;
                    break;
                case DOWN:
                    if (leftHeavy) {
                        startPositions.add(bottomRight(x, y, type));
                        startPositions.add(middleLeft(x, y, type));
                        startPositions.add(topRight(x, y, type));
                    } else {
                        startPositions.add(bottomLeft(x, y, type));
                        startPositions.add(middleRight(x, y, type));
                        startPositions.add(topLeft(x, y, type));
                    }
                    count += 3;
                    break;
                case LEFT:
                    if (leftHeavy) {
                        startPositions.add(bottomLeft(x, y, type));
                        startPositions.add(topMiddle(x, y, type));
                        startPositions.add(bottomRight(x, y, type));
                    } else {
                        startPositions.add(topLeft(x, y, type));
                        startPositions.add(bottomMiddle(x, y, type));
                        startPositions.add(topRight(x, y, type));
                    }
                    count += 3;
                    break;
                case RIGHT:
                    if (leftHeavy) {
                        startPositions.add(topRight(x, y, type));
                        startPositions.add(bottomMiddle(x, y, type));
                        startPositions.add(topLeft(x, y, type));
                    } else {
                        startPositions.add(bottomRight(x, y, type));
                        startPositions.add(topMiddle(x, y, type));
                        startPositions.add(bottomLeft(x, y, type));
                    }
                    count += 3;
                    break;
            }
            leftHeavy = !leftHeavy;
            // Go backwards
            do {
                index = MathUtils.wrap(index - 1, 0, pieces.size());
                piece = pieces.get(index);
            }
            // Whilst we are on corners
            while (piece.getType().isCorner());
        }
        return startPositions;
    }

    private static Vector3f topLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y + OFFSET, (float) type.getAngle());
    }

    private static Vector3f topMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y + OFFSET, (float) type.getAngle());
    }

    private static Vector3f topRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y + OFFSET, (float) type.getAngle());
    }

    private static Vector3f middleLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y, (float) type.getAngle());
    }

    private static Vector3f middleMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y, (float) type.getAngle());
    }

    private static Vector3f middleRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y, (float) type.getAngle());
    }

    private static Vector3f bottomLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y - OFFSET, (float) type.getAngle());
    }

    private static Vector3f bottomMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y - OFFSET, (float) type.getAngle());
    }

    private static Vector3f bottomRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y - OFFSET, (float) type.getAngle());
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

        do {
            pieces.add(piece);
            edges.add(new TrackEdges(piece));
            // Check the type of the current piece
            switch (grid[tempX][tempY].getType().finalDirection()) {
                // Go up
                case UP:
                    tempY++;
                    break;
                // Go down
                case DOWN:
                    tempY--;
                    break;
                // Go left
                case LEFT:
                    tempX--;
                    break;
                // Go right
                case RIGHT:
                    tempX++;
                    break;
            }
            piece = grid[tempX][tempY];
        } while (!(tempX == startX && tempY == startY));
    }

    private void findStartingPoint() {
        // Choose a random non-corner piece
        // TODO choose a long straight edge
        var piece = randomPiece();
        startPieceX = piece.getFirst();
        startPieceY = piece.getSecond();
        while (pieceGrid[startPieceX][startPieceY].getType().isCorner()) {
            piece = randomPiece();
            startPieceX = piece.getFirst();
            startPieceY = piece.getSecond();
        }

    }

    private Pair<Integer, Integer> randomPiece() {
        var x = MathUtils.randomInteger(0, pieceGrid.length);
        var y = MathUtils.randomInteger(0, pieceGrid[0].length);

        var piece = pieceGrid[x][y];
        if (piece == null) {
            return randomPiece();
        }
        return new Pair<>(x, y);
    }

}
