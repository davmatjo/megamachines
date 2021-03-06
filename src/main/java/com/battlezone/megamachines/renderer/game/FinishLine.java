package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.renderer.theme.ThemeHandler;
import com.battlezone.megamachines.renderer.ui.Colour;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.track.TrackPiece;
import com.battlezone.megamachines.world.track.TrackType;

import static org.lwjgl.opengl.GL11.*;

/**
 * Creates a drawable background
 */
public class FinishLine implements Drawable {

    /**
     * Size of the background
     */
    private static final float SCALE = 4.51f;
    private static final Model model = Model.SQUARE;
    private static final int indexCount = model.getIndices().length;
    /**
     * x position of the background
     */
    private float x;
    /**
     * y position of the background
     */
    private float y;
    private boolean needsRotate;
    /**
     * Temporary matrix for transformations that prevents new object creation
     */
    private Matrix4f tempMatrix = new Matrix4f();
    /**
     * Texture used for the background
     */
    private Texture texture = AssetManager.loadTexture(ThemeHandler.getTheme() + "/tracks/start.png");

    public FinishLine(TrackPiece piece) {
        needsRotate = piece.getType() == TrackType.LEFT || piece.getType() == TrackType.RIGHT;

        this.x = (float) piece.getX();
        this.y = (float) piece.getY();
    }

    /**
     * Draws the background by sending the scale, and position matrices to the GPU
     */
    @Override
    public void draw() {
        getShader().setVector4f("spriteColour", Colour.WHITE);
        getShader().setMatrix4f("rotation", Matrix4f.rotationZ(getAngle(), tempMatrix));
        getShader().setMatrix4f("size", Matrix4f.scale(SCALE, tempMatrix));
        getShader().setInt("sampler", 0);
        texture.bind();

        getShader().setMatrix4f("position", Matrix4f.translate(Matrix4f.IDENTITY, x, y, 0f, tempMatrix));
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }

    private float getAngle() {
        return needsRotate ? 90 : 0;
    }

    @Override
    public Model getModel() {
        return model;
    }

    /**
     * Shader used for this object
     *
     * @return Entity shader
     */
    @Override
    public Shader getShader() {
        return Shader.CAR;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    /**
     * Set the position of the background, this can enable scrolling
     *
     * @param x x coordinate to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Set the position of the background, this can enable scrolling
     *
     * @param y y coordinate to set
     */
    public void setY(float y) {
        this.y = y;
    }
}
