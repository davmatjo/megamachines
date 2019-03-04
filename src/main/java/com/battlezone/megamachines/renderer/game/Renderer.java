package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.util.Pair;

import java.util.*;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;

/**
 * Manages the shaders for all renderable objects
 */
public class Renderer {

    private final Map<Model, ModelBinding> modelBindings = new HashMap<>();

    private final List<Pair<Shader, List<Pair<Model, List<Drawable>>>>> toRender = new ArrayList<>();

    private final List<Drawable> drawables = new ArrayList<>();

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

//        DrawableRenderer renderer = new DrawableRenderer(drawable);

//        Shader shader = renderer.getShader();
//        if (renderables.containsKey(shader)) {
//            renderables.get(shader).add(renderer);
//        } else {
//            renderables.put(shader, new ArrayList<>() {{add(renderer);}});
//        }
        if (!modelBindings.containsKey(drawable.getModel())) {
            modelBindings.put(drawable.getModel(), new ModelBinding(drawable.getModel()));
        }
        drawables.add(drawable);
        drawables.sort(Comparator.comparing(Drawable::getShader).thenComparing(Drawable::getModel));
        populateRenderables();
    }

    public void removeDrawable(Drawable drawable) {
//        List<DrawableRenderer> drawableRenderers = renderables.get(drawable.getShader());
//        if (drawableRenderers != null) {
//            var toRemove = new DrawableRenderer(drawable);
//            var toRemoveIndex = drawableRenderers.indexOf(toRemove);
//            if (toRemoveIndex != -1) {
//                drawableRenderers.get(toRemoveIndex).delete();
//                drawableRenderers.remove(toRemoveIndex);
//            } else {
//                System.err.println("Item was not in render list");
//            }
//            toRemove.delete();
//        } else {
//            System.err.println("Attempt to remove non existant object");
//        }

        drawables.remove(drawable);
        populateRenderables();
    }

    private void populateRenderables() {
        toRender.clear();
        Shader currentShader = null;
        Model currentModel = null;
        int i = -1;
        int j = -1;
        for (var d : drawables) {
            if (!d.getShader().equals(currentShader)) {
                currentShader = d.getShader();
                currentModel = null;
                j = -1;
                toRender.add(new Pair<>(d.getShader(), new ArrayList<>()));
                i++;
            }
            if (!d.getModel().equals(currentModel)) {
                currentModel = d.getModel();
                toRender.get(i).getSecond().add(new Pair<>(d.getModel(), new ArrayList<>()));
                j++;
            }
            toRender.get(i).getSecond().get(j).getSecond().add(d);
        }
    }

    /**
     * Binds the correct shader, sets the projection and renders each object for that shader
     */
    public void render(double interval) {
        for (int i = 0; i < animatables.size(); i++) {
            animatables.get(i).animate(interval);
        }
//        renderables.keySet().forEach(shader -> {
//            shader.use();
//            shader.setMatrix4f("projection", camera.getProjection());
//            for (var renderable : renderables.get(shader)) {
//                renderable.render();
//            }
//        });
//        Shader currentShader = null;
//        Model currentModel = null;

//        for (int i=0; i<drawables.size(); i++) {
//            var drawable = drawables.get(i);
//            if (!drawable.getShader().equals(currentShader)) {
//                currentShader = drawable.getShader();
//                currentShader.use();
//                currentShader.setMatrix4f("projection", camera.getProjection());
//            }
//            if (!drawable.getModel().equals(currentModel)) {
//                currentModel = drawable.getModel();
//                modelBindings.get(currentModel).bind();
//            }
//            drawable.draw();
//        }
        for (int i = 0; i< toRender.size(); i++) {
            var r = toRender.get(i);
            r.getFirst().use();
            r.getFirst().setMatrix4f("projection", camera.getProjection());
            for (int j=0; j<r.getSecond().size(); j++) {
                var s = r.getSecond().get(j);
                modelBindings.get(s.getFirst()).bind();
                for (int k=0; k < s.getSecond().size(); k++) {
                    s.getSecond().get(k).draw();
                }
            }
        }
    }
}
