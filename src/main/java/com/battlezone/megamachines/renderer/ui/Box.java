package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.math.Vector3f;
import com.battlezone.megamachines.math.Vector4f;
import com.battlezone.megamachines.renderer.game.*;
import com.battlezone.megamachines.util.AssetManager;

import static org.lwjgl.opengl.GL30.*;

public class Box extends AbstractRenderable implements Drawable {

    private final Vector4f colour;
    private static final Shader shader = Shader.STATIC;
    Texture texture = AssetManager.loadTexture("/cars/car1.png");

    public Box(float width, float height, float x, float y, Vector4f colour) {
        super(new Model(
                new float[]{
                        x, y + height, 0,
                        x + width, y + height, 0,
                        x + width, y, 0,
                        x, y, 0},
                new int[] {
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

    @Override
    public void draw() {
        texture.bind();
        Shader.STATIC.setVector4f("colour", colour);
        Shader.STATIC.setMatrix4f("texturePosition", Matrix4f.scale(0.5f).translate(0f, 1f, 0f));
        glDrawElements(GL_TRIANGLES, getIndexCount(), GL_UNSIGNED_INT, 0);
    }

    @Override
    public Shader getShader() {
        return shader;
    }

}
