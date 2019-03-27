package com.battlezone.megamachines.renderer.ui.menu;

import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.util.Pair;

import java.util.ArrayList;

public class TrackEditor implements Renderable {

    private boolean[][] track;
    private Pair<Integer, Integer> cursor;

    private float x, y, width, height;
    private int size;

    private ArrayList<Box> trackBoxes = new ArrayList<>();
    private Box cursorBox;

    private float cellSize;

    private boolean editing = false;
    private long lastFlash = System.currentTimeMillis();
    private boolean showingCursor = true;

    public TrackEditor(int size, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.size = size;

        this.cellSize = width / size;

        this.track = new boolean[size][size];
        this.cursor = new Pair<>(0, 0);
        this.cursorBox = new Box(cellSize, cellSize, x, y, Colour.GREY);
    }

    public void moveCursor(int x, int y) {
        var oldCursor = new Pair<>(cursor.getFirst(), cursor.getSecond());
        var newX = cursor.getFirst() + x;
        var newY = cursor.getSecond() + y;
        if (newX >= 0 && newY >= 0 && newX < track.length && newY < track[0].length) {
            this.cursor.set(newX, newY);
            var screenCoordinates = map(this.cursor);
            cursorBox.setPos(screenCoordinates.getFirst(), screenCoordinates.getSecond());
            if (editing) {
                //if current pos has a piece, and old pos has a piece, delete from the old place and dont touch new place (we're deleting track backwards)
                if (track[oldCursor.getFirst()][oldCursor.getSecond()] && track[cursor.getFirst()][cursor.getSecond()])
                    togglePiece(oldCursor);
                else
                    togglePiece(cursor);
            }
        }
    }

    public void toggleEditing() {
        editing = !editing;
    }

    public boolean isEditing() {
        return editing;
    }

    private void togglePiece(Pair<Integer, Integer> position) {
        var x = position.getFirst();
        var y = position.getSecond();
        track[x][y] = !track[x][y];

        regenerateBoxes();
    }

    private void regenerateBoxes() {
        trackBoxes.clear();
        for (int i = 0; i < track.length; i++) {
            var row = track[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j]) {
                    var pos = map(new Pair<>(i, j));
                    Box box = new Box(cellSize, cellSize, pos.getFirst(), pos.getSecond(), Colour.WHITE);
                    trackBoxes.add(box);
                }
            }
        }
    }

    /**
     * Maps from the internal grid system to the screen coordinates
     *
     * @param pos Internal coordinates
     * @return Screen coordinates
     */
    private Pair<Float, Float> map(Pair<Integer, Integer> pos) {
        var size = (float) track.length;
        var res = new Pair<>(pos.getFirst() / size * width + x, pos.getSecond() / size * height + y);
        return res;
    }

    @Override
    public void render() {
        for (int i = 0; i < trackBoxes.size(); i++) {
            trackBoxes.get(i).render();
        }

        if (System.currentTimeMillis() - lastFlash > 300) {
            showingCursor = !showingCursor;
            lastFlash = System.currentTimeMillis();
        }
        if (showingCursor)
            cursorBox.render();
    }

    @Override
    public Shader getShader() {
        return null;
    }

    @Override
    public void delete() {

    }

    public boolean[][] getBoolGrid() {
        return track;
    }

    public void reset() {
        this.track = new boolean[size][size];
        this.cursor = new Pair<>(0, 0);
        this.editing = false;
        this.regenerateBoxes();
        this.moveCursor(0, 0);
    }
}
