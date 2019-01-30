package com.battlezone.megamachines.renderer;

/**
 * An object is drawable if it can provide its shader and draw itself if the correct buffers are bound
 */
public interface Drawable {

    /**
     * Draw this object to the screen
     */
    void draw();

    Model getModel();

    /**
     * @return The shader this object needs active to draw itself
     */
    Shader getShader();
}
