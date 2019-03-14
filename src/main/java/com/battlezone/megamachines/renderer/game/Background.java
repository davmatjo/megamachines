package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.util.AssetManager;

import static org.lwjgl.opengl.GL11.*;

/**
 * Creates a drawable background
 */
public class Background implements Drawable {

    /**
     * x position of the background
     */
    private float x = 0;

    /**
     * y position of the background
     */
    private float y = 0;

    /**
     * Temporary matrix for transformations that prevents new object creation
     */
    private Matrix4f tempMatrix = new Matrix4f();

    /**
     * Size of the background
     */
    private static final float SCALE = 10f;

    private static final int TILE_COUNT = 11;

    /**
     * Texture used for the background
     */
    private final Texture texture = AssetManager.loadTexture(ThemeHandler.getTheme() + "/tracks/background_1.png");

    private static final Model model = Model.SQUARE;
    private static final int indexCount = model.getIndices().length;

    /**
     * Draws the background by sending the scale, and position matrices to the GPU
     */
    @Override
    public void draw() {
        getShader().setMatrix4f("size", Matrix4f.scale(SCALE, tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();
        for (int i=-1; i<TILE_COUNT; i++) {
            for (int j=-1; j <TILE_COUNT; j++) {
                getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, i * SCALE*2 + x, j * SCALE*2 + y, 0f, tempMatrix));
                glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
            }
        }
    }

    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Shader used for this object
     * @return Entity shader
     */
    @Override
    public Shader getShader() {
        return Shader.ENTITY;
    }

    /**
     * Set the position of the background, this can enable scrolling
     * @param x x coordinate to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Set the position of the background, this can enable scrolling
     * @param y y coordinate to set
     */
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public int getDepth() {
        return -10;
    }
}
