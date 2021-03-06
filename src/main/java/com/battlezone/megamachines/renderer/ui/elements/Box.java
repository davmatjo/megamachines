package com.battlezone.megamachines.renderer.ui.elements;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.*;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * A simple 2d box
 */
public class Box implements Renderable, Drawable {

    private static final Shader shader = Shader.STATIC;
    private final int indexCount;
    private final float width, height;
    private Vector4f colour;
    private DrawableRenderer drawableRenderer;
    private Texture texture = Texture.BLANK;
    private Model model;

    /**
     * @param width  Width of the box
     * @param height Height of the box
     * @param x      X-coordinate of the left of the box
     * @param y      Y coordinate of the bottom of the box
     * @param colour The colour of the box, a vector representing rgba
     */
    public Box(float width, float height, float x, float y, Vector4f colour) {
        this.width = width;
        this.height = height;
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
                }, 0);

        this.drawableRenderer = new DrawableRenderer(this);
        this.colour = colour;
        this.indexCount = model.getIndices().length;
    }

    /**
     * @param width   Width of the box
     * @param height  Height of the box
     * @param x       X-coordinate of the left of the box
     * @param y       Y coordinate of the bottom of the box
     * @param colour  The colour of the box, a vector representing rgba
     * @param texture The texture to draw on the box
     */
    public Box(float width, float height, float x, float y, Vector4f colour, Texture texture) {
        this.width = width;
        this.height = height;
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
                }, 0);
//        super(Model.generateSquare());
        this.drawableRenderer = new DrawableRenderer(this);
        this.texture = texture;
        this.colour = colour;
        this.indexCount = model.getIndices().length;
    }

    /**
     * Changed the position of the box
     *
     * @param x New x coordinate of the left side
     * @param y New y coordinate of the bottom
     */
    public void setPos(float x, float y) {
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
                }, 0);
        this.drawableRenderer = new DrawableRenderer(this);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
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

    @Override
    public int getDepth() {
        return 0;
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
