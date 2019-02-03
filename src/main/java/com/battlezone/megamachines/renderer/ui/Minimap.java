package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.Track;

import java.util.ArrayList;
import java.util.List;

public class Minimap extends Box {

    private final List<Pair<RWDCar, DrawableRenderer>> players = new ArrayList<>();
    private static final float PLAYER_WIDTH = 0.05f;
    private static final float PLAYER_HEIGHT = 0.05f;
    private final Texture minimapCar = Texture.BLANK;
    private final Matrix4f position = new Matrix4f();
    private final float scaleX;
    private final float scaleY;

    public Minimap(float width, float height, float x, float y, Track track, List<RWDCar> cars) {
        super(width, height, x, y, new Vector4f(1f, 0f, 0f, 1f), AssetManager.loadTexture(track.generateMinimap()));
        float trackWidth = track.getTracksAcross() * track.getTrackSize();
        float trackHeight = track.getTracksDown() * track.getTrackSize();
        this.scaleX = width / trackWidth;
        this.scaleY = width / trackHeight;
        for (var car : cars) {
            players.add(new Pair<>(
                    car,
                    new DrawableRenderer(new Box(PLAYER_WIDTH, PLAYER_HEIGHT, 0, 0, new Vector4f(1, 1, 1, 1)))));
        }
    }

    @Override
    public void draw() {
        super.draw();
        drawCars();
    }

    private void drawCars() {
        for (var set : players) {
            float x = set.getFirst().getXf() * scaleX;
            float y = set.getFirst().getYf() * scaleY;
            Shader.STATIC.setMatrix4f("position", Matrix4f.translate(Scene.STATIC_PROJECTION, x, y, 0, position));
            set.getSecond().render();
        }
        Shader.STATIC.setMatrix4f("position", Scene.STATIC_PROJECTION);
    }
}
