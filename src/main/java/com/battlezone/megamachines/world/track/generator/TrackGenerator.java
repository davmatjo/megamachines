package com.battlezone.megamachines.world.track.generator;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.networking.server.Server;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.ArrayList;
import java.util.List;

public abstract class TrackGenerator {

    private final static float OFFSET = ScaleController.TRACK_SCALE / 6;
    final int tracksAcross, tracksDown;
    TrackType[][] grid;
    private List<TrackPiece> pieces;
    private TrackPiece[][] pieceGrid;
    private int finishPieceX, finishPieceY;

    /**
     * Creates a track generator with the given width and height.
     *
     * @param tracksAcross The width of the track.
     * @param tracksDown   The height of the track.
     */
    TrackGenerator(int tracksAcross, int tracksDown) {
        this.tracksAcross = tracksAcross;
        this.tracksDown = tracksDown;

        grid = new TrackType[tracksAcross][tracksDown];
        pieceGrid = new TrackPiece[tracksAcross][tracksDown];
        pieces = new ArrayList<>();
    }

    /**
     * A method to create a list of starting positions from a given finish piece and the list representation of a track.
     *
     * @param finishPiece The finishing piece of a track.
     * @param pieces      The list representation of a track.
     * @return A list of starting positions in order.
     */
    public static List<Vector3f> calculateStartingPositions(TrackPiece finishPiece, List<TrackPiece> pieces) {
        List<Vector3f> startPositions = new ArrayList<>();
        boolean leftHeavy = true;
        int index = MathUtils.wrap(pieces.indexOf(finishPiece) - 1, 0, pieces.size());
        TrackPiece piece = pieces.get(index);
        int count = 0;
        int reqCount = Server.MAX_PLAYERS;
        // Keep going whilst there are spaces to fill
        while (count < reqCount) {
            // Get the current piece's co-ordinates
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
                // Diagonals
                case UP_LEFT:
                case RIGHT_DOWN:
                case LEFT_UP:
                case DOWN_RIGHT:
                    startPositions.add(bottomLeft(x, y, type));
                    startPositions.add(middleMiddle(x, y, type));
                    startPositions.add(topRight(x, y, type));
                    break;
                case UP_RIGHT:
                case LEFT_DOWN:
                case RIGHT_UP:
                case DOWN_LEFT:
                    startPositions.add(bottomRight(x, y, type));
                    startPositions.add(middleMiddle(x, y, type));
                    startPositions.add(topLeft(x, y, type));
                    break;
            }
            // Flip whether they should be piled towards the left
            leftHeavy = !leftHeavy;
            // Get the next piece for the next iteration
            index = MathUtils.wrap(index - 1, 0, pieces.size());
            piece = pieces.get(index);
        }
        return startPositions;
    }

    /**
     * A method to get the top left position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the top left of the piece.
     */
    private static Vector3f topLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y + OFFSET, (float) type.getAngle());
    }

    /**
     * A method to get the top middle position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the top middle of the piece.
     */
    private static Vector3f topMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y + OFFSET, (float) type.getAngle());
    }

    /**
     * A method to get the top right position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the top right of the piece.
     */
    private static Vector3f topRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y + OFFSET, (float) type.getAngle());
    }

    /**
     * A method to get the middle left position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the middle left of the piece.
     */
    private static Vector3f middleLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y, (float) type.getAngle());
    }

    /**
     * A method to get the middle position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the top left of the piece.
     */
    private static Vector3f middleMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y, (float) type.getAngle());
    }

    /**
     * A method to get the middle right position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the middle right of the piece.
     */
    private static Vector3f middleRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y, (float) type.getAngle());
    }

    /**
     * A method to get the bottom left position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the bottom left of the piece.
     */
    private static Vector3f bottomLeft(float x, float y, TrackType type) {
        return new Vector3f(x - OFFSET, y - OFFSET, (float) type.getAngle());
    }

    /**
     * A method to get the bottom middle position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the bottom middle of the piece.
     */
    private static Vector3f bottomMiddle(float x, float y, TrackType type) {
        return new Vector3f(x, y - OFFSET, (float) type.getAngle());
    }

    /**
     * A method to get the bottom right position for a piece as well as its angle.
     *
     * @param x    The X coordinate of the piece in the world.
     * @param y    The Y coordinate of the piece in the world.
     * @param type The type of track piece that it is.
     * @return The position and angle in the bottom right of the piece.
     */
    private static Vector3f bottomRight(float x, float y, TrackType type) {
        return new Vector3f(x + OFFSET, y - OFFSET, (float) type.getAngle());
    }

    /**
     * A method to populate a map of track pieces from a map of track types.
     *
     * @param types        The map of track types to use.
     * @param pieces       The map of track pieces to store in.
     * @param tracksAcross The number of track across.
     * @param tracksDown   The number of tracks down.
     * @return The map of track pieces.
     */
    public static TrackPiece[][] typeToPieceGrid(TrackType[][] types, TrackPiece[][] pieces, final int tracksAcross, final int tracksDown) {
        for (int x = 0; x < tracksAcross; x++)
            for (int y = 0; y < tracksDown; y++)
                if (types[x][y] != null)
                    pieces[x][y] = new TrackPiece(x * ScaleController.TRACK_SCALE, y * ScaleController.TRACK_SCALE, types[x][y]);
        return pieces;
    }

    /**
     * A method to populate a list of track pieces in order from a map of track pieces with a starting position.
     *
     * @param pieces The list of track pieces to populate.
     * @param grid   The map of track pieces to use.
     * @param startX The X coordinate of the starting piece.
     * @param startY The Y coordinate of the starting piece.
     */
    public static void populateListInOrder(List<TrackPiece> pieces, TrackPiece[][] grid, final int startX, final int startY) {
        // Start at the beginning
        int tempX = startX, tempY = startY;
        // Create the first piece
        TrackPiece piece = grid[startX][startY];

        do {
            pieces.add(piece);
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

    /**
     * A method to choose a random coordinate of which contains a piece of track from a map of track types.
     *
     * @param grid The map of track types.
     * @return The generated coordinate.
     */
    public static Pair<Integer, Integer> randomPiece(TrackType[][] grid) {
        int x, y;
        do {
            x = MathUtils.randomInteger(0, grid.length);
            y = MathUtils.randomInteger(0, grid[0].length);
        } while (grid[x][y] == null);
        return new Pair<>(x, y);
    }

    /**
     * A method to generate the final Track.
     *
     * @return The track it has generated.
     */
    public Track generateTrack() {
        generateMap();
        typeToPieceGrid(grid, pieceGrid, tracksAcross, tracksDown);
        findStartingPoint();
        populateListInOrder(pieces, pieceGrid, finishPieceX, finishPieceY);
        List<Vector3f> startGrid = calculateStartingPositions(pieceGrid[finishPieceX][finishPieceY], pieces);
        return new Track(pieces, grid, pieceGrid, finishPieceX, finishPieceY, startGrid);
    }

    /**
     * The method used to generate a map.
     */
    abstract void generateMap();

    /**
     * The method to find a starting point that isn't on a corner, updates internal fields.
     */
    private void findStartingPoint() {
        // Choose a random non-corner piece
        var piece = randomPiece();
        finishPieceX = piece.getFirst();
        finishPieceY = piece.getSecond();
        while (pieceGrid[finishPieceX][finishPieceY].getType().isCorner()) {
            piece = randomPiece();
            finishPieceX = piece.getFirst();
            finishPieceY = piece.getSecond();
        }

    }

    /**
     * A method to generate the coordinate of a random piece from the map.
     *
     * @return The coordinate of the randomly chosen piece.
     */
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
