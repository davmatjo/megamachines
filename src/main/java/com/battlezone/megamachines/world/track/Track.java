package com.battlezone.megamachines.world.track;

import com.battlezone.megamachines.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Track {

    private List<TrackPiece> pieces;
    private TrackType[][] grid;
    private TrackPiece[][] pieceGrid;
    private final int tracksAcross, tracksDown;
    private final int trackSize;
    private final int startPieceX, startPieceY;
    private final List<TrackEdges> edges;

    public Track(List<TrackPiece> _pieces, TrackType[][] _grid, TrackPiece[][] _pieceGrid, int _trackSize, int _startPieceX, int _startPieceY, List<TrackEdges> _edges) {
        pieces = _pieces;
        grid = _grid;
        pieceGrid = _pieceGrid;
        trackSize = _trackSize;
        startPieceX = _startPieceX;
        startPieceY = _startPieceY;
        edges = _edges;
        tracksAcross = grid.length;
        tracksDown = grid[0].length;
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

    public int getTrackSize() {
        return trackSize;
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
}
