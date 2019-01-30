package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;

import static org.lwjgl.opengl.GL30.*;

/**
 * Reference implementation of an AbstractRenderable
 */
public class GenericDrawable implements Drawable {

    private static final Shader shader = Shader.ENTITY;
    private final Texture texture;
    private float x;
    private float y;
    private float scale;
    private Matrix4f tempMatrix = new Matrix4f();
    private final Model model;
    private final int indexCount;

    GenericDrawable(Model model, Texture texture, float x, float y, float scale) {
        this.model = model;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.indexCount = model.getIndices().length;
    }

    @Override
    public void draw() {
        texture.bind();
        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, x, y, 0f, tempMatrix));
        getShader().setMatrix4f("scale", Matrix4f.scale(scale, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
