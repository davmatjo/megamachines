package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.*;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;

import static org.lwjgl.opengl.GL30.*;

public class Box implements Renderable, Drawable {

    private Vector4f colour;
    private final DrawableRenderer drawableRenderer;
    private static final Shader shader = Shader.STATIC;
    private Texture texture = Texture.BLANK;
    private final Model model;
    private final int indexCount;

    public Box(float width, float height, float x, float y, Vector4f colour) {
        model = new Model(
                new float[]{
                        x, y + height, 0,
                        x + width, y + height, 0,
                        x + width, y, 0,
                        x, y, 0},
                new int[]{
                        0, 1, 2,
                        2, 3, 0
                },
                new float[]{
                        0, 0,
                        1, 0,
                        1, 1,
                        0, 1,
                });

        this.drawableRenderer = new DrawableRenderer(this);
        this.colour = colour;
        this.indexCount = model.getIndices().length;
    }

    public Box(float width, float height, float x, float y, Vector4f colour, Texture texture) {
        model = new Model(
                new float[]{
                        x, y + height, 0,
                        x + width, y + height, 0,
                        x + width, y, 0,
                        x, y, 0},
                new int[]{
                        0, 1, 2,
                        2, 3, 0
                },
                new float[]{
                        0, 0,
                        1, 0,
                        1, 1,
                        0, 1,
                });
//        super(Model.generateSquare());
        this.drawableRenderer = new DrawableRenderer(this);
        this.texture = texture;
        this.colour = colour;
        this.indexCount = model.getIndices().length;
    }

    @Override
    public void draw() {
        texture.bind();
        Shader.STATIC.setVector4f("colour", colour);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    @Override
    public Model getModel() {
        return model;
    }

    @Override
    public void render() {
        drawableRenderer.render();
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    public void setColour(Vector4f colour) {
        this.colour = colour;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void delete() {
        drawableRenderer.delete();
    }
}
