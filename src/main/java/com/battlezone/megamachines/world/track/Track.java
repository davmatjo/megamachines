package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.util.ArrayUtil;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Track implements Serializable {

    private final List<TrackPiece> pieces;
    private final TrackType[][] grid;
    private final TrackPiece[][] pieceGrid;
    private final int tracksAcross, tracksDown;
    private final int finishPieceX, finishPieceY;
    private final List<Vector3f> startingPositions;

    /**
     * Creates a track from a list of track pieces, a map of types, a map of track pieces, the finishing position and
     * a list of starting positions.
     *
     * @param _pieces            The list of track pieces.
     * @param _grid              The map of track types.
     * @param _pieceGrid         The map of track pieces.
     * @param _finishPieceX      The X coordinate of the finishing piece.
     * @param _finishPieceY      The Y coordinate of the finishing piece.
     * @param _startingPositions The list of starting positions.
     */
    public Track(List<TrackPiece> _pieces, TrackType[][] _grid, TrackPiece[][] _pieceGrid, int _finishPieceX, int _finishPieceY, List<Vector3f> _startingPositions) {
        pieces = _pieces;
        grid = _grid;
        pieceGrid = _pieceGrid;
        finishPieceX = _finishPieceX;
        finishPieceY = _finishPieceY;
        tracksAcross = grid.length;
        tracksDown = grid[0].length;
        startingPositions = _startingPositions;
    }

    /**
     * The minimal constructor of a track. It creates what it needs from a map of track types, width and finishing
     * position coordinates.
     *
     * @param _grid         The map of track types.
     * @param _tracksAcross The width of the track.
     * @param _finishPieceX The X coordinate of the finishing piece.
     * @param _finishPieceY The Y coordinate of the finishing piece.
     */
    public Track(TrackType[][] _grid, int _tracksAcross, int _finishPieceX, int _finishPieceY) {
        grid = _grid;
        tracksAcross = _tracksAcross;
        tracksDown = grid[0].length;
        pieceGrid = TrackGenerator.typeToPieceGrid(grid, new TrackPiece[tracksAcross][tracksDown], tracksAcross, tracksDown);
        finishPieceX = _finishPieceX;
        finishPieceY = _finishPieceY;
        pieces = new ArrayList<>();
        TrackGenerator.populateListInOrder(pieces, pieceGrid, finishPieceX, finishPieceY);
        startingPositions = TrackGenerator.calculateStartingPositions(pieceGrid[finishPieceX][finishPieceY], pieces);
    }

    /**
     * A method to create a track from a byte array.
     *
     * @param byteArray The byte array to read from.
     * @param offset    The offset of where the track is held in the byte array.
     * @return
     */
    public static Track fromByteArray(byte[] byteArray, int offset) {
        int trackAcross = byteArray[offset];
        int trackDown = byteArray[offset + 1];
        int startX = byteArray[offset + 2];
        int startY = byteArray[offset + 3];
        TrackType[][] grid = new TrackType[trackAcross][trackDown];

        for (int i = 0; i < trackAcross; i++)
            for (int j = 0; j < trackDown; j++)
                grid[i][j] = TrackType.fromByte(byteArray[offset + 4 + i * trackAcross + j]);

        Track track = new Track(grid, trackAcross, startX, startY);

        return track;
    }

    /**
     * A method to determine whether a map of track types will result in a valid track.
     *
     * @param grid The map of track types.
     * @return Whether this map of track types would create a valid track.
     */
    public static boolean isValidTrack(TrackType[][] grid) {
        return minimumDimensions(grid) && allSameLength(grid) && noAdjacentPieces(grid) && isLoop(grid) && noFloatingPieces(grid);
    }

    /**
     * A method to determine whether the dimensions of the grid are consistent.
     *
     * @param grid The map of track types.
     * @return Whether the dimensions are consistent.
     */
    private static boolean allSameLength(TrackType[][] grid) {
        int firstLength = grid[0].length;
        for (int i = 1; i < grid.length; i++)
            if (grid[1].length != firstLength)
                return false;
        return true;
    }

    /**
     * A method to determine whether the map of types satisfies the minimum dimensions of 3x3.
     *
     * @param grid The map of track types.
     * @return Whether the track would be >= 3x3.
     */
    private static boolean minimumDimensions(TrackType[][] grid) {
        final int width = grid.length;
        if (width < 3)
            return false;
        final int height = grid[0].length;
        return height > 2;
    }

    /**
     * A method to determine whether there are no extra adjacent pieces to any piece within the given map.
     *
     * @param grid The map of track types.
     * @return Whether all pieces have the correct number of adjacent pieces.
     */
    private static boolean noAdjacentPieces(TrackType[][] grid) {
        final int width = grid.length, height = grid[0].length;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (grid[x][y] != null && countAround(grid, width, height, x, y) != 2)
                    return false;
        return true;
    }

    /**
     * A method to count how many pieces are around a piece in a map given the coordinates.
     *
     * @param grid   The map to count the neighbouring pieces in.
     * @param width  The width of the map.
     * @param height The height of the map.
     * @param x      The X coordinate of the point to count around.
     * @param y      The Y coordinate of the point to count around.
     * @return How many adjacent pieces there are to the given point.
     */
    private static int countAround(TrackType[][] grid, int width, int height, int x, int y) {
        int count = 0;
        if (x < width - 1 && grid[x + 1][y] != null) count++;
        if (x > 0 && grid[x - 1][y] != null) count++;
        if (y < height - 1 && grid[x][y + 1] != null) count++;
        if (y > 0 && grid[x][y - 1] != null) count++;
        return count;
    }

    /**
     * A method to determine whether the given map successfully loops from a random position.
     *
     * @param grid The map to test.
     * @return Whether the given map is a loop.
     */
    private static boolean isLoop(TrackType[][] grid) {
        final Pair<Integer, Integer> piece = TrackGenerator.randomPiece(grid);
        final int startX = piece.getFirst(), startY = piece.getSecond();
        int x = startX, y = startY;
        try {
            do {
                TrackType type = grid[x][y];
                switch (type.finalDirection()) {
                    case UP:
                        if (grid[x][++y].initialDirection() == TrackType.UP)
                            break;
                        else return false;
                    case DOWN:
                        if (grid[x][--y].initialDirection() == TrackType.DOWN)
                            break;
                        else return false;
                    case LEFT:
                        if (grid[--x][y].initialDirection() == TrackType.LEFT)
                            break;
                        else return false;
                    case RIGHT:
                        if (grid[++x][y].initialDirection() == TrackType.RIGHT)
                            break;
                        else return false;
                }
            } while (!(x == startX && y == startY));
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * A method to determine whether there are no pieces detached from any part of a given map.
     *
     * @param grid The map of track types.
     * @return Whether there are no pieces detached from any other part of the track.
     */
    private static boolean noFloatingPieces(TrackType[][] grid) {
        int across = grid.length, down = grid[0].length;
        TrackPiece[][] piecesGrid = new TrackPiece[across][down];
        piecesGrid = TrackGenerator.typeToPieceGrid(grid, piecesGrid, across, down);
        List<TrackPiece> tempPieces = new ArrayList<>();
        final Pair<Integer, Integer> piece = TrackGenerator.randomPiece(grid);
        TrackGenerator.populateListInOrder(tempPieces, piecesGrid, piece.getFirst(), piece.getSecond());
        // Generated track piece list should be the same size as non-null grid elements
        return tempPieces.size() == countPieces(grid);
    }

    /**
     * A method to count the pieces in a map of track types.
     *
     * @param grid The map of track types.
     * @return The number of pieces that are defined.
     */
    private static int countPieces(TrackType[][] grid) {
        int count = 0;
        for (int x = 0; x < grid.length; x++)
            for (int y = 0; y < grid[x].length; y++)
                if (grid[x][y] != null)
                    count++;
        return count;
    }

    /**
     * A method to create a map of track types from a map of boolean values.
     *
     * @param boolGrid The map of boolean values.
     * @return The map of track types.
     */
    public static TrackType[][] createFromBoolGrid(boolean[][] boolGrid) {
        try {
            var track = new TrackType[boolGrid.length][boolGrid[0].length];

            for (int i = 0; i < boolGrid.length; i++) {
                for (int j = 0; j < boolGrid[0].length; j++) {
                    track[i][j] = boolGrid[i][j] ? TrackType.UP : null;
                }
            }

            var trues = getTrue(boolGrid).toArray();

            for (int i = 0; i < trues.length; i++)
                if (getAround(boolGrid, (Pair<Integer, Integer>) trues[i]).size() != 2)
                    throw new RuntimeException();

            var start = (Pair<Integer, Integer>) ArrayUtil.randomElement(trues);

            var pos = start;
            var adjs = getAround(boolGrid, start);
            var next = adjs.get(0);
            var prev = adjs.get(1);
            do {
                var type = getType(pos, next, prev);
                track[pos.getFirst()][pos.getSecond()] = type;
                prev = pos;
                pos = next;
                var around = getAround(boolGrid, pos);
                next = around.get(0);
                if (next.equals(prev))
                    next = around.get(1);
            } while (!pos.equals(start));

            return track;
        } catch (RuntimeException e) {
            return new TrackType[0][0];
        }
    }

    /**
     * A method to get a list of coordinates of adjacent track pieces to a given piece.
     *
     * @param grid The map of booleans.
     * @param pos  The coordinate to check from.
     * @return A list of coordinates of adjacent track pieces to a given piece.
     */
    public static ArrayList<Pair<Integer, Integer>> getAround(boolean[][] grid, Pair<Integer, Integer> pos) {
        var left = new Pair<>(pos.getFirst() - 1, pos.getSecond());
        var right = new Pair<>(pos.getFirst() + 1, pos.getSecond());
        var above = new Pair<>(pos.getFirst(), pos.getSecond() + 1);
        var below = new Pair<>(pos.getFirst(), pos.getSecond() - 1);

        var res = new ArrayList<Pair<Integer, Integer>>();

        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, left.getFirst(), left.getSecond())))
            res.add(left);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, right.getFirst(), right.getSecond())))
            res.add(right);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, above.getFirst(), above.getSecond())))
            res.add(above);
        if (Boolean.TRUE.equals(ArrayUtil.safeGet(grid, below.getFirst(), below.getSecond())))
            res.add(below);

        return res;
    }

    /**
     * A method to get a list of coordinates of true values from a boolean map.
     *
     * @param array The map of booleans.
     * @return A list of coordinates of true values from a boolean map.
     */
    public static ArrayList<Pair<Integer, Integer>> getTrue(boolean[][] array) {
        ArrayList<Pair<Integer, Integer>> res = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j]) {
                    res.add(new Pair<>(i, j));
                }
            }
        }

        return res;
    }

    /**
     * A method to get the track type from a position given the next and previous coordinates.
     *
     * @param pos  The position of track type that needs calculating.
     * @param next The coordinate that comes after this track piece.
     * @param prev The coordiante that comes before this track piece.
     * @return The type of the track at the given position.
     */
    private static TrackType getType(Pair<Integer, Integer> pos, Pair<Integer, Integer> next, Pair<Integer, Integer> prev) {
        var initial = directionOf(prev, pos);
        var end = directionOf(pos, next);
        var type = TrackType.fromDirections(initial, end);
        return type;
    }

    /**
     * A method to determine the partial direction of a track given two coordinates.
     *
     * @param pos  The position to end at.
     * @param from The position to start from.
     * @return The direction between the two. Either UP, DOWN, LEFT or RIGHT.
     */
    private static TrackType directionOf(Pair<Integer, Integer> pos, Pair<Integer, Integer> from) {
        if (pos.getFirst().equals(from.getFirst())) {
            if (pos.getSecond() - from.getSecond() == 1) {
                return TrackType.DOWN;
            }
            return TrackType.UP;
        }

        if (pos.getFirst() - from.getFirst() == 1) {
            return TrackType.LEFT;
        }
        return TrackType.RIGHT;
    }

    /**
     * A method to get the list of track pieces from a Track.
     *
     * @return The list of track pieces.
     */
    public List<TrackPiece> getPieces() {
        return pieces;
    }

    /**
     * A method to get the map of track types from a Track.
     *
     * @return The map of track types.
     */
    public TrackType[][] getGrid() {
        return grid;
    }

    /**
     * A method to get the map of track pieces from a Track.
     *
     * @return The map of track pieces.
     */
    public TrackPiece[][] getPieceGrid() {
        return pieceGrid;
    }

    /**
     * A method to get the piece from a given index.
     *
     * @param index The index of the piece to retrieve.
     * @return The piece found at that index of the track.
     */
    public TrackPiece getPiece(int index) {
        return getPieces().get(index);
    }

    /**
     * A method to get the width of a track.
     *
     * @return The width of the track.
     */
    public int getTracksAcross() {
        return tracksAcross;
    }

    /**
     * A method to get the height of a track.
     *
     * @return The height of the track.
     */
    public int getTracksDown() {
        return tracksDown;
    }

    /**
     * A method to get the finishing piece of a track.
     *
     * @return The finishing piece.
     */
    public TrackPiece getFinishPiece() {
        return pieceGrid[finishPieceX][finishPieceY];
    }

    /**
     * A method to get the starting positions of a track.
     *
     * @return The starting positions of a track.
     */
    public List<Vector3f> getStartingPositions() {
        return startingPositions;
    }

    /**
     * A method to get the piece before the finishing piece of a track.
     *
     * @return The piece before the finishing piece.
     */
    public TrackPiece getBeforeFinishPiece() {
        return pieces.get(MathUtils.wrap(pieces.indexOf(getFinishPiece()) - 1, 0, pieces.size()));
    }

    /**
     * Creates a BufferedImage of the track's layout.
     *
     * @return the BufferedImage of the track's layout.
     */
    public BufferedImage generateMinimap() {
        Vector4f colour = ThemeHandler.getTheme().uiFontColour();
        Color primary = new Color(colour.x, colour.y, colour.z, colour.w);
        return generateMinimap(primary, Color.GRAY);
    }

    /**
     * Creates a BufferedImage of the track's layout.
     *
     * @return the BufferedImage of the track's layout.
     */
    public BufferedImage generateMinimap(Color color, Color secondaryColor) {

        BufferedImage trackImg = new BufferedImage(tracksAcross, tracksDown, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = trackImg.createGraphics();

        // Fill background with transparency
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.drawRect(0, 0, tracksAcross, tracksDown);

        // Change to white to prepare to draw the track
        g2d.setColor(color);

        // Loop over track pieces
        for (int i = 0; i < tracksAcross; i++) {
            for (int j = 0; j < tracksDown; j++) {
                if (grid[i][j] != null) {
                    // Calculate top left corner
                    final int offsetX = i;
                    final int offsetY = (tracksDown - j) - 1;
                    // Draw the track piece
                    g2d.drawRect(offsetX, offsetY, 0, 0);
                }
            }
        }

        // Draw start piece
        g2d.setColor(secondaryColor);
        g2d.drawRect(finishPieceX, tracksDown - finishPieceY - 1, 0, 0);

        // Dispose the graphics context
        g2d.dispose();

        return trackImg;
    }

    /**
     * A method to convert a track into its byte array representation.
     *
     * @return The byte array representation of the track.
     */
    public byte[] toByteArray() {
        // Explanation: we need 4 bytes for: tracksAcross, tracksDown, startX, startY; then we need tracksDown*tracksAcross for each trackType.
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + tracksDown * tracksAcross);
        byteBuffer.put((byte) tracksAcross).put((byte) tracksDown).put((byte) finishPieceX).put((byte) finishPieceY);
        for (int i = 0; i < tracksAcross; i++)
            for (int j = 0; j < tracksDown; j++)
                if (grid[i][j] == null)
                    byteBuffer.put((byte) (-1));
                else
                    byteBuffer.put(grid[i][j].toByte());
        return byteBuffer.array();
    }

    /**
     * Creates a string that visually represents the track.
     *
     * @return A string that visually represents the track.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = tracksDown - 1; y >= 0; y--) {
            for (int x = 0; x < tracksAcross; x++)
                sb.append(grid[x][y] != null ? ts(grid[x][y]) : "  ");
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * A method to get the appropriate character to visualise the track type.
     *
     * @param type The type of track to visualise.
     * @return The character to visualise this track type.
     */
    private String ts(TrackType type) {
        switch (type) {
            case UP:
            case DOWN:
                return "| ";
            case LEFT:
            case RIGHT:
                return "- ";
            case UP_RIGHT:
            case LEFT_DOWN:
                return "⌜ ";
            case UP_LEFT:
            case RIGHT_DOWN:
                return "⌝ ";
            case DOWN_RIGHT:
            case LEFT_UP:
                return "⌞ ";
            case DOWN_LEFT:
            case RIGHT_UP:
                return "⌟ ";
        }
        return "";
    }

}
