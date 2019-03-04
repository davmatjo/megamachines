package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.animation.Animatable;

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
     * List of animatable drawable objects
     */
    private List<Animatable> animatables = new ArrayList<>();

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
    public void addDrawable(Drawable drawable) {

        if (drawable instanceof Animatable) {
            animatables.add((Animatable) drawable);
        }

        DrawableRenderer renderer = new DrawableRenderer(drawable);

        Shader shader = renderer.getShader();
        if (renderables.containsKey(shader)) {
            renderables.get(shader).add(renderer);
        } else {
            renderables.put(shader, new ArrayList<>() {{add(renderer);}});
        }
    }

    public void removeDrawable(Drawable drawable) {
        List<DrawableRenderer> drawableRenderers = renderables.get(drawable.getShader());
        if (drawableRenderers != null) {
            var toRemove = new DrawableRenderer(drawable);
            var toRemoveIndex = drawableRenderers.indexOf(toRemove);
            if (toRemoveIndex != -1) {
                drawableRenderers.get(toRemoveIndex).delete();
                drawableRenderers.remove(toRemoveIndex);
            } else {
                System.err.println("Item was not in render list");
            }
            toRemove.delete();
        } else {
            System.err.println("Attempt to remove non existent object");
        }
    }

    /**
     * Binds the correct shader, sets the projection and renders each object for that shader
     */
    public void render(double interval) {
        for (int i = 0; i < animatables.size(); i++) {
            animatables.get(i).animate(interval);
        }
        renderables.keySet().forEach(shader -> {
            shader.use();
            shader.setMatrix4f("projection", camera.getProjection());
            for (var renderable : renderables.get(shader)) {
                renderable.render();
            }
        });
    }
}
