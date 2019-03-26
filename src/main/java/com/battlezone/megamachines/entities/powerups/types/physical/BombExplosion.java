package com.battlezone.megamachines.entities.powerups.types.physical;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.AnimatedTexture;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.Renderer;
import com.battlezone.megamachines.util.AssetManager;

import static org.lwjgl.opengl.GL11.*;

public class BombExplosion implements Drawable {

    private static final int MAX_RENDER_COUNT = 8;
    private static final float SCALE = 3f;
    private static final int indexCount = Model.SQUARE.getIndices().length;
    private final float x;
    private final float y;
    private final AnimatedTexture cloudTexture = AssetManager.loadAnimation("/effects/explode_", MAX_RENDER_COUNT, 14, false);
    private final Renderer renderer;
    private final Matrix4f tempMatrix = new Matrix4f();

    public BombExplosion(Renderer renderer, float x, float y) {
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        cloudTexture.setFrame(0);
        renderer.addDrawable(this);
    }

    @Override
    public void draw() {
        if (cloudTexture.getCurrentFrame() < MAX_RENDER_COUNT) {
            cloudTexture.bind();
            getShader().setMatrix4f("size", Matrix4f.scale(SCALE, tempMatrix));
            getShader().setInt("sampler", 0);
            getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, x, y, 0f, tempMatrix));
            glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        } else {
            renderer.removeDrawable(this);
        }
    }

    @Override
    public Model getModel() {
        return Model.SQUARE;
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    @Override
    public int getDepth() {
        return 0;
    }
}
