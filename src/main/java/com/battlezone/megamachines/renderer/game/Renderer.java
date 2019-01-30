package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Drawable;

import java.util.*;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;

/**
 * Manages the shaders for all renderable objects
 */
public class Renderer {

    /**
     * Links the shader to each of its renderable objects
     */
    private Map<Shader, List<DrawableRenderer>> renderables = new LinkedHashMap<>();

    /**
     * Camera to provide the projection for all these objects
     */
    private final Camera camera;

    /**
     * Create a new renderer with this camera
     * @param camera camera used for projections
     */
    public Renderer(Camera camera) {
        this.camera = camera;
    }

    /**
     * Add a new drawable object to be drawn by this renderer
     * @param drawable drawable to draw each frame
     */
    public void addRenderable(Drawable drawable) {

        DrawableRenderer renderer = new DrawableRenderer(drawable);

        Shader shader = renderer.getShader();
        if (renderables.containsKey(shader)) {
            renderables.get(shader).add(renderer);
        } else {
            renderables.put(shader, new ArrayList<>() {{add(renderer);}});
        }
    }

    /**
     * Binds the correct shader, sets the projection and renders each object for that shader
     */
    public void render() {

        renderables.keySet().forEach(shader -> {
            shader.use();
            shader.setMatrix4f("projection", camera.getProjection());
            for (var renderable : renderables.get(shader)) {
                renderable.render();
            }
        });
    }
}
