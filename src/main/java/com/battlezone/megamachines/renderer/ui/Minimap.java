package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.entities.RWDCar;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.util.Pair;
import com.battlezone.megamachines.world.BaseWorld;
import com.battlezone.megamachines.world.ScaleController;
import com.battlezone.megamachines.world.track.Track;

import java.util.ArrayList;
import java.util.List;

public class Minimap extends Box {

    private final List<Pair<RWDCar, DrawableRenderer>> players = new ArrayList<>();
    private static final float PLAYER_WIDTH = 0.05f;
    private static final float PLAYER_HEIGHT = 0.05f;
    public static final float MAP_WIDTH = 0.8f, MAP_HEIGHT = 0.8f,
            MAP_X = Window.getWindow().getRight() - MAP_WIDTH - BaseWorld.PADDING, MAP_Y = Window.getWindow().getTop() - MAP_HEIGHT - BaseWorld.PADDING;
    private final Texture minimapCar = Texture.BLANK;
    private final Matrix4f position = new Matrix4f();
    private final float scaleX;
    private final float scaleY;
    private float mapX;
    private float mapY;

    public Minimap(Track track, List<RWDCar> cars) {
        super(MAP_WIDTH, MAP_HEIGHT, MAP_X, MAP_Y, new Vector4f(1f, 1f, 1f, 0.5f), AssetManager.loadTexture(track.generateMinimap()));
        float trackWidth = track.getTracksAcross() * ScaleController.TRACK_SCALE;
        float trackHeight = track.getTracksDown() * ScaleController.TRACK_SCALE;
        this.scaleX = MAP_WIDTH / trackWidth;
        this.scaleY = MAP_HEIGHT / trackHeight;
        this.mapX = MAP_X;
        this.mapY = MAP_Y;
        for (var car : cars) {
            players.add(new Pair<>(
                    car,
                    new DrawableRenderer(new Box(PLAYER_WIDTH, PLAYER_HEIGHT, 0, 0, car.getColour(), Texture.CIRCLE))));
        }
    }

    @Override
    public void draw() {
        super.draw();
        drawCars();
    }

    public void setX(float x) {
        this.mapX = x;
        super.setPos(mapX, mapY);
    }

    public void setY(float y) {
        this.mapY = y;
        super.setPos(mapX, mapY);
    }

    public void setPos(float x, float y) {
        this.mapX = x;
        this.mapY = y;
        super.setPos(mapX, mapY);
    }

    private void drawCars() {
        for (int i = 0; i < players.size(); i++) {
            Pair<RWDCar, DrawableRenderer> set = players.get(i);
            float x = mapX + (PLAYER_WIDTH / 2) + set.getFirst().getXf() * scaleX;
            float y = mapY + (PLAYER_HEIGHT / 2) + set.getFirst().getYf() * scaleY;
            Shader.STATIC.setMatrix4f("position", Matrix4f.translate(Scene.STATIC_CAMERA.getProjection(), x, y, 0, position));
            set.getSecond().render();
        }
        Shader.STATIC.setMatrix4f("position", Scene.STATIC_CAMERA.getProjection());
    }
}
