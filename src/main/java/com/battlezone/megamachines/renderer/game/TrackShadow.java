package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.world.GameObject;
import com.battlezone.megamachines.world.track.Track;

import static org.lwjgl.opengl.GL11.*;

public class TrackShadow extends TrackSet {

    private static final Shader shader = Shader.ENTITY;
    private Matrix4f tempMatrix = new Matrix4f();
    private final Camera camera;

    public TrackShadow(Camera camera) {
        this.camera = camera;
        super.setTheme("/shadow");
    }

    @Override
    public void draw() {
        shader.setMatrix4f("size", Matrix4f.scale(scale, tempMatrix));
        shader.setInt("sampler", 0);
        filteredTracks.forEach((type, trackSet) -> {
            trackTextures.get(type).bind();
            trackSet.forEach((track) -> {
                shader.setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, camera.getX() / 10f + track.getXf() - 5f, camera.getY() / 10f + track.getYf() - 5f, 0f, tempMatrix));
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
            });
        });
    }

    @Override
    public void setTrack(Track track) {
        super.setTrack(track);
        scale = track.getPiece(0).getScale() / (2);

    }


    @Override
    public int getDepth() {
        return -1;
    }
}
