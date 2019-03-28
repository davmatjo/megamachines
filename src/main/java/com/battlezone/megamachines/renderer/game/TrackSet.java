package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.Track;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

/**
 * The TrackSet class is responsible for drawing a whole set of track pieces
 */
public class TrackSet implements Drawable {

    private static final Shader shader = Shader.ENTITY;
    final Map<TrackType, List<TrackPiece>> filteredTracks = new HashMap<>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, new ArrayList<>());
        }
    }};
    final int indexCount;
    private final Model model;
    float scale = 0;
    private String theme = ThemeHandler.getTheme().toString();
    final Map<TrackType, Texture> trackTextures = new HashMap<>() {{
        for (TrackType trackType : TrackType.values()) {
            put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }};
    private Matrix4f tempMatrix = new Matrix4f();

    /**
     * Creates a new TrackSet
     */
    public TrackSet() {
        this.model = Model.SQUARE;
        this.indexCount = model.getIndices().length;
    }

    /**
     * Set the track that this TrackSet should draw. Removes any previous tracks
     * @param track Track to draw
     */
    public void setTrack(Track track) {
        filteredTracks.values().forEach(List::clear);
        track.getPieces().forEach((e) -> filteredTracks.get(e.getType()).add(e));
        if (track.getPieces().size() > 0) {
            scale = track.getPiece(0).getScale() / 2;
        }
    }

    /**
     * @param theme The theme that this TrackSet should draw
     */
    public void setTheme(String theme) {
        this.theme = theme;
        trackTextures.clear();
        for (TrackType trackType : TrackType.values()) {
            trackTextures.put(trackType, AssetManager.loadTexture(theme + trackType.getFileName()));
        }
    }

    /**
     * Draws a track set onto the screen, assuming the projection has been bound
     */
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
    public int getDepth() {
        return 0;
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
