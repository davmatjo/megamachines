package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.util.AssetManager;

import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import static org.lwjgl.opengl.GL30.*;

public class Box extends AbstractRenderable {

    private Vector4f colour;
    private static final Shader shader = Shader.STATIC;
    private Texture texture = Texture.BLANK;

    public Box(float width, float height, float x, float y, Vector4f colour) {
        super(new Model(
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
                })
        );
//        super(Model.generateSquare());
        this.colour = colour;
    }

    public Box(float width, float height, float x, float y, Vector4f colour, Texture texture) {
        super(new Model(
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
                })
        );
//        super(Model.generateSquare());
        this.texture = texture;
        this.colour = colour;
    }

    @Override
    public void draw() {
        texture.bind();
        Shader.STATIC.setVector4f("colour", colour);
//        Shader.STATIC.setMatrix4f("texturePosition", new Matrix4f(1/29f, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f, 0, 0, 0, 0, 1f).translate(28f, 0f, 0));
        glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public Shader getShader() {
        return shader;
    }

    public void setColour(Vector4f colour) {
        this.colour = colour;
    }
}
