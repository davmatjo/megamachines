package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.world.track.Track;

import static com.battlezone.megamachines.world.BaseWorld.PARALLAX;
import static org.lwjgl.opengl.GL11.*;

/**
 * A TrackShadow is a specialisation of TrackSet that draws the track smaller and with a different texture
 */
public class TrackShadow extends TrackSet {

    private static final Shader shader = Shader.ENTITY;
    private static final float TRACK_SCALE = 0.5f;
    private final Camera camera;
    private Matrix4f tempMatrix = new Matrix4f();

    /**
     * Creates a new track shadow
     *
     * @param camera The camera position, for correct shadow positioning
     */
    public TrackShadow(Camera camera) {
        this.camera = camera;
        super.setTheme(ThemeHandler.getTheme() + "/shadows");
    }

    /**
     * Draws the current Track's shadow
     */
    @Override
    public void draw() {
        shader.setMatrix4f("size", Matrix4f.scale(scale, tempMatrix));
        shader.setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                shader.setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, camera.getX() / PARALLAX + track.getXf() * TRACK_SCALE - 5f,
                        camera.getY() / PARALLAX + track.getYf() * TRACK_SCALE - 5f, 0f, tempMatrix));
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
            });
        });
    }

    /**
     * @param track Track to draw
     */
    @Override
    public void setTrack(Track track) {
        super.setTrack(track);
        scale = track.getPiece(0).getScale() * (TRACK_SCALE / 2f);

    }


    @Override
    public int getDepth() {
        return -1;
    }
}
