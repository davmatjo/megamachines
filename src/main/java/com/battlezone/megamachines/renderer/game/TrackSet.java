package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackPiece;
import com.battlezone.megamachines.world.TrackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class TrackSet implements Drawable {

    private static final Shader shader = Shader.ENTITY;
    private final Map<TrackType, List<TrackPiece>> filteredTracks = new HashMap<TrackType, List<TrackPiece>>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, new ArrayList<>());
        }
    }};
    private String theme = "/";
    private final Map<TrackType, Texture> trackTextures = new HashMap<TrackType, Texture>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }};
    private float scale = 0;
    private Matrix4f tempMatrix = new Matrix4f();
    private final Model model;
    private final int indexCount;


    public TrackSet(Model model) {
        this.model = model;
        this.indexCount = model.getIndices().length;
    }

    public void setTrack(Track track) {
        filteredTracks.values().forEach(List::clear);
        track.getPieces().forEach((e) -> filteredTracks.get(e.getType()).add(e));
        if (track.getPieces().size() > 0) {
            scale = track.getPiece(0).getScale() / 2;
        }
    }

    public void setTheme(String theme) {
        this.theme = theme;
        trackTextures.clear();
        for (TrackType trackType : TrackType.values()) {
            trackTextures.put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }

    @Override
    public void draw() {
        shader.setMatrix4f("size", Matrix4f.scale(scale, tempMatrix));
        shader.setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                shader.setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, track.getXf(), track.getYf(), 0f, tempMatrix));
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
            });
        });
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return shader;
    }
}
