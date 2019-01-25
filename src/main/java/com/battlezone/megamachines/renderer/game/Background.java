package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.util.AssetManager;

import static org.lwjgl.opengl.GL11.*;

public class Background extends AbstractRenderable {

    private static final float SCALE = 100f;
    private static final Texture texture = AssetManager.loadTexture("/tracks/background_1.png");
    private float x = 0;
    private float y = 0;
    private Matrix4f tempMatrix = new Matrix4f();

    public Background() {
        super(Model.generateSquare());
    }

    @Override
    public void draw() {
        getShader().setMatrix4f("size", Matrix4f.scale(SCALE, tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();
        getShader().setMatrix4f("position", Matrix4f.translate(x, y, 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
