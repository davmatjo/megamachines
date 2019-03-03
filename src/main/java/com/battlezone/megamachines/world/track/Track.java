package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.math.MathUtils;
import com.battlezone.megamachines.math.Vector3f;
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

    // Minimal constructor
    private Track(TrackType[][] _grid, int _tracksAcross, int _finishPieceX, int _finishPieceY) {
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

    public List<TrackPiece> getPieces() {
        return pieces;
    }

    public TrackType[][] getGrid() {
        return grid;
    }

    public TrackPiece[][] getPieceGrid() {
        return pieceGrid;
    }

    public TrackPiece getPiece(int index) {
        return getPieces().get(index);
    }

    public int getTracksAcross() {
        return tracksAcross;
    }

    public int getTracksDown() {
        return tracksDown;
    }

    public TrackPiece getFinishPiece() {
        return pieceGrid[finishPieceX][finishPieceY];
    }

    public TrackPiece getBeforeFinishPiece() {
        return pieces.get(MathUtils.wrap(pieces.indexOf(getFinishPiece()) - 1, 0, pieces.size()));
    }


    /**
     * Creates a BufferedImage of the track's layout.
     *
     * @return the BufferedImage of the track's layout.
     */
    public BufferedImage generateMinimap() {
        return generateMinimap(Color.WHITE, Color.GRAY);
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

    public List<Vector3f> getStartingPositions() {
        return startingPositions;
    }

    public static boolean isValidTrack(TrackType[][] grid) {
        return allSameLength(grid) && isLoop(grid) && noFloatingPieces(grid);
    }

    private static boolean allSameLength(TrackType[][] grid) {
        int firstLength = grid[0].length;
        for (int i = 1; i < grid.length; i++)
            if (grid[1].length != firstLength)
                return false;
        return true;
    }

    private static boolean isLoop(TrackType[][] grid) {
        final Pair<Integer, Integer> piece = TrackGenerator.randomPiece(grid);
        final int startX = piece.getFirst(), startY = piece.getSecond();
        int x = startX, y = startY;
        int count = 0;
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
            e.printStackTrace();
            return false;
        }
        return true;
    }

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

    private static int countPieces(TrackType[][] grid) {
        int count = 0;
        for (int x = 0; x < grid.length; x++)
            for (int y = 0; y < grid[x].length; y++)
                if (grid[x][y] != null)
                    count++;
        return count;
    }

}
