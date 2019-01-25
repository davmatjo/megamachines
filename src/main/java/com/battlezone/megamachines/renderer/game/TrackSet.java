package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackPiece;
import com.battlezone.megamachines.world.TrackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class TrackSet extends AbstractRenderable {

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
    private Camera camera;


    public TrackSet(Model model, Camera camera) {
        super(model);

        this.camera = camera;
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
//        getShader().use();
//        getShader().setMatrix4f("projection", camera.getProjection());
        shader.setMatrix4f("size", Matrix4f.scale(scale, new Matrix4f()));
        shader.setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                shader.setMatrix4f("position", Matrix4f.translate(track.getXf(), track.getYf(), 0f, new Matrix4f()));
                glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
            });
        });
        glUseProgram(0);
    }

    @Override
    public Shader getShader() {
        return shader;
    }
}
