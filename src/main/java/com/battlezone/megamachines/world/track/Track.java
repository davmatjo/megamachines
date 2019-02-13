package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.generator.TrackGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Track {

    private List<TrackPiece> pieces;
    private TrackType[][] grid;
    private TrackPiece[][] pieceGrid;
    private final int tracksAcross, tracksDown;
    private final int startPieceX, startPieceY;
    private final List<TrackEdges> edges;

    public Track(List<TrackPiece> _pieces, TrackType[][] _grid, TrackPiece[][] _pieceGrid, int _startPieceX, int _startPieceY, List<TrackEdges> _edges) {
        pieces = _pieces;
        grid = _grid;
        pieceGrid = _pieceGrid;
        startPieceX = _startPieceX;
        startPieceY = _startPieceY;
        edges = _edges;
        tracksAcross = grid.length;
        tracksDown = grid[0].length;
    }

    // Minimal constructor
    private Track(TrackType[][] _grid, int _tracksAcross, int _startPieceX, int _startPieceY) {
        grid = _grid;
        tracksAcross = _tracksAcross;
        tracksDown = grid[0].length;
        pieceGrid = TrackGenerator.typeToPieceGrid(grid, new TrackPiece[tracksAcross][tracksDown], tracksAcross, tracksDown);
        startPieceX = _startPieceX;
        startPieceY = _startPieceY;
        edges = new ArrayList<>();
        pieces = new ArrayList<>();
        TrackGenerator.populateListInOrder(pieces, edges, pieceGrid, startPieceX, startPieceY);
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

    public float getTrackSize() {
        return ScaleController.TRACK_SCALE;
    }

    public TrackPiece getStartPiece() {
        return pieceGrid[startPieceX][startPieceY];
    }

    /**
     * Creates a BufferedImage of the track's layout.
     *
     * @return the BufferedImage of the track's layout.
     */
    public BufferedImage generateMinimap() {

        BufferedImage trackImg = new BufferedImage(tracksAcross * 3, tracksDown * 3, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = trackImg.createGraphics();

        // Fill background with transparency
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.drawRect(0, 0, tracksAcross * 3, tracksDown * 3);

        // Change to white to prepare to draw the track
        g2d.setColor(Color.WHITE);

        // Loop over track pieces
        for (int i = 0; i < tracksAcross; i++) {
            for (int j = 0; j < tracksDown; j++) {
                if (grid[i][j] != null) {
                    // Calculate top left corner of the 3x3 grid
                    final int offsetX = i * 3;
                    final int offsetY = (tracksDown - j) * 3 - 3;
                    // Draw the different types of track
                    switch (grid[i][j]) {
                        case DOWN:
                        case UP:
                            // Draw straight vertical line
                            g2d.drawRect(offsetX + 1, offsetY, 1, 3);
                            break;
                        case LEFT:
                        case RIGHT:
                            // Draw straight horizontal line
                            g2d.drawRect(offsetX, offsetY + 1, 3, 1);
                            break;
                        case RIGHT_UP:
                        case DOWN_LEFT:
                            // Draw _| line
                            g2d.drawRect(offsetX, offsetY + 1, 2, 1);
                            g2d.drawRect(offsetX + 1, offsetY, 1, 1);
                            break;
                        case LEFT_UP:
                        case DOWN_RIGHT:
                            // Draw |_ line
                            g2d.drawRect(offsetX + 1, offsetY + 1, 2, 1);
                            g2d.drawRect(offsetX + 1, offsetY, 1, 1);
                            break;
                        case UP_RIGHT:
                        case LEFT_DOWN:
                            // Draw |- line
                            g2d.drawRect(offsetX + 1, offsetY + 1, 2, 1);
                            g2d.drawRect(offsetX + 1, offsetY + 2, 1, 1);
                            break;
                        case UP_LEFT:
                        case RIGHT_DOWN:
                            // Draw -| line
                            g2d.drawRect(offsetX, offsetY + 1, 2, 1);
                            g2d.drawRect(offsetX + 1, offsetY + 2, 1, 1);
                            break;
                    }
                }
            }
        }

        // Dispose the graphics context
        g2d.dispose();

        return trackImg;
    }

    public List<TrackEdges> getEdges() {
        return edges;
    }

    public byte[] toByteArray() {
        // Explanation: we need 4 bytes for: tracksAcross, tracksDown, startX, startY; then we need tracksDown*tracksAcross for each trackType.
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + tracksDown * tracksAcross);
        byteBuffer.put((byte) tracksAcross).put((byte) tracksDown).put((byte) startPieceX).put((byte) startPieceY);
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
}
