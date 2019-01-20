package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackType;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class TrackRenderer extends Renderer {

    private String theme = "/";
    private float scale = 0;

    private final Map<TrackType, List<Track>> filteredTracks = new HashMap<TrackType, List<Track>>() {{
        for (TrackType trackType : TrackType.values()) {
                put(trackType, new ArrayList<>());
        }
    }};

    private final Map<TrackType, Texture> trackTextures = new HashMap<TrackType, Texture>() {{
        for (TrackType trackType : TrackType.values()) {
                put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }};


    public TrackRenderer(Model model, Shader shader) {
        super(model, shader);
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
        getShader().setMatrix4f("size", new Matrix4f().scale(scale));
        getShader().setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                getShader().setMatrix4f("position", new Matrix4f().translate(track.getPosition().x, track.getPosition().y, 0f));
                glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
            });
        });

    }
}
