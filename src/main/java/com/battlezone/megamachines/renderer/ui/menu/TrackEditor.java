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

    private ArrayList<Box> trackBoxes = new ArrayList<>();
    private Box cursorBox;

    private float cellSize;

    private boolean editing = false;

    public TrackEditor(int size, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.cellSize = width / size;

        this.track = new boolean[size][size];
        this.cursor = new Pair<>(0, 0);
        this.cursorBox = new Box(cellSize, cellSize, x, y, Colour.GREY);
    }

    public void moveCursor(int x, int y) {
        System.out.println("move");
        var newX = cursor.getFirst() + x;
        var newY = cursor.getSecond() + y;
        if (newX >= 0 && newY >= 0 && newX < track.length && newY < track[0].length) {
            this.cursor.set(newX, newY);
            var screenCoordinates = map(this.cursor);
            cursorBox.setPos(screenCoordinates.getFirst(), screenCoordinates.getSecond());
            if (editing)
                layPiece();
        }
    }

    public void beginEditing() {
        editing = true;
    }

    private void layPiece() {
        var x = cursor.getFirst();
        var y = cursor.getSecond();
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

    private long lastFlash = System.currentTimeMillis();
    private boolean showingCursor = true;

    @Override
    public void render() {
        if (System.currentTimeMillis() - lastFlash > 300) {
            showingCursor = !showingCursor;
            lastFlash = System.currentTimeMillis();
        }
        if (showingCursor)
            cursorBox.render();
        for (int i = 0; i < trackBoxes.size(); i++) {
            trackBoxes.get(i).render();
        }
    }

    @Override
    public Shader getShader() {
        return null;
    }

    @Override
    public void delete() {

    }
}
