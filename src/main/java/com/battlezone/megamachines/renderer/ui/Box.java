package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.game.*;

import static org.lwjgl.opengl.GL30.*;

public class Box implements Drawable {

    private Vector4f colour;
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
        this.texture = texture;
        this.colour = colour;
        this.indexCount = model.getIndices().length;
    }

    @Override
    public void draw() {
        texture.bind();
        Shader.STATIC.setVector4f("colour", colour);
//        Shader.STATIC.setMatrix4f("texturePosition", new Matrix4f(1/29f, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f).translate(28f, 0f, 0));
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

    public void setColour(Vector4f colour) {
        this.colour = colour;
    }
}
