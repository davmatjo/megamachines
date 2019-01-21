package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class TrackSet extends AbstractRenderable {

    private final Map<TrackType, List<Track>> filteredTracks = new HashMap<>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, new ArrayList<>());
        }
    }};
    private String theme = "/";
    private final Map<TrackType, Texture> trackTextures = new HashMap<>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }};
    private float scale = 0;
    private Camera camera;


    public TrackSet(Model model, Camera camera) {
        super(model, AssetManager.loadShader("/shaders/entity"));
        this.camera = camera;
    }

    public void setTrack(List<Track> track) {
        filteredTracks.values().forEach(List::clear);
        track.forEach((e) -> filteredTracks.get(e.getType()).add(e));
        if (track.size() > 0) {
            scale = track.get(0).getScale() / 2;
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
        getShader().setMatrix4f("size", Matrix4f.scale(scale));
        getShader().setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                getShader().setMatrix4f("position", new Matrix4f().translate(track.getXf(), track.getYf(), 0f));
                glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
            });
        });
        glUseProgram(0);
    }
}
