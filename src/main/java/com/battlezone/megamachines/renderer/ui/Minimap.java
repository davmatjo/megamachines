package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.List;

public class Minimap extends Box {

    private final List<Pair<RWDCar, DrawableRenderer>> players = new ArrayList<>();
    private static final float PLAYER_WIDTH = 0.05f;
    private static final float PLAYER_HEIGHT = 0.05f;
    private static final float MINIMAP_WIDTH = 0.08f;
    private static final float MINIMAP_HEIGHT = 0.08f;
    private static final float MINIMAP_X = 0.8f;
    private static final float MINIMAP_Y = 0f;
    private final Texture minimapCar = Texture.BLANK;
    private final Matrix4f position = new Matrix4f();
    private final float scaleX;
    private final float scaleY;
    private final float mapX;
    private final float mapY;

    public Minimap(Track track, List<RWDCar> cars) {
        super(MINIMAP_WIDTH * track.getTracksAcross(),
                MINIMAP_HEIGHT * track.getTracksDown(),
                MINIMAP_X,
                MINIMAP_Y,
                new Vector4f(1f, 1f, 1f, 0.5f),
                AssetManager.loadTexture(track.generateMinimap()));
        float trackWidth = track.getTracksAcross() * track.getTrackSize();
        float trackHeight = track.getTracksDown() * track.getTrackSize();
        this.scaleX = MINIMAP_WIDTH * track.getTracksAcross() / trackWidth;
        this.scaleY = MINIMAP_HEIGHT * track.getTracksDown() / trackHeight;
        this.mapX = MINIMAP_X;
        this.mapY = MINIMAP_Y;
        for (var car : cars) {
            players.add(new Pair<>(
                    car,
                    new DrawableRenderer(new Box(PLAYER_WIDTH, PLAYER_HEIGHT, 0, 0, new Vector4f(car.getColour(),1), Texture.CIRCLE))));
        }
    }

    @Override
    public void draw() {
        super.draw();
        drawCars();
    }

    private void drawCars() {
        for (var set : players) {
            float x = mapX + (PLAYER_WIDTH / 2) + set.getFirst().getXf() * scaleX;
            float y = mapY + (PLAYER_HEIGHT / 2) + set.getFirst().getYf() * scaleY;
            Shader.STATIC.setMatrix4f("position", Matrix4f.translate(Scene.STATIC_PROJECTION, x, y, 0, position));
            set.getSecond().render();
        }
        Shader.STATIC.setMatrix4f("position", Scene.STATIC_PROJECTION);
    }
}
