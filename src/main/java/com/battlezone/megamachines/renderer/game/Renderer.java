package com.battlezone.megamachines.renderer.game;

import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.game.animation.Animatable;
import com.battlezone.megamachines.util.Pair;

import java.util.*;

/**
 * Manages the shaders for all renderable objects
 */
public class Renderer {

    private static Renderer instance;
    private final Map<Model, ModelBinding> modelBindings = new HashMap<>();
    private final List<Pair<Integer, List<Pair<Shader, List<Pair<Model, List<Drawable>>>>>>> toRender = new ArrayList<>();
    private final List<Drawable> drawables = new ArrayList<>();
    /**
     * Camera to provide the projection for all these objects
     */
    private final Camera camera;
    /**
     * List of animatable drawable objects
     */
    private List<Animatable> animatables = new ArrayList<>();

    /**
     * Create a new renderer with this camera
     *
     * @param camera camera used for projections
     */
    public Renderer(Camera camera) {
        this.camera = camera;
        instance = this;
    }

    public static Renderer getInstance() {
        return instance;
    }

    /**
     * Add a new drawable object to be drawn by this renderer
     *
     * @param drawable drawable to draw each frame
     */
    public void addDrawable(Drawable drawable) {

        if (drawable instanceof Animatable) {
            animatables.add((Animatable) drawable);
        }

        if (!modelBindings.containsKey(drawable.getModel())) {
            modelBindings.put(drawable.getModel(), new ModelBinding(drawable.getModel()));
        }
        drawables.add(drawable);
        populateRenderables();
    }

    /**
     * Removes a drawable from this renderer
     * @param drawable Drawable to remove
     */
    public void removeDrawable(Drawable drawable) {
        drawables.remove(drawable);
        populateRenderables();
    }

    /**
     * Sorts all objects currently active in the renderer by depth then shader then model. Places the results in toRender
     */
    public void populateRenderables() {
        drawables.sort(Comparator.comparing(Drawable::getDepth).thenComparing(Drawable::getShader).thenComparing(Drawable::getModel));
        toRender.clear();
        Shader currentShader = null;
        Model currentModel = null;
        int currentDepth = Integer.MIN_VALUE;
        int i = -1;
        int j = -1;
        int k = -1;
        for (var d : drawables) {
            if (d.getDepth() != currentDepth) {
                currentDepth = d.getDepth();
                currentModel = null;
                currentShader = null;
                j = -1;
                k = -1;
                toRender.add(new Pair<>(d.getDepth(), new ArrayList<>()));
                i++;
            }
            if (!d.getShader().equals(currentShader)) {
                currentShader = d.getShader();
                currentModel = null;
                k = -1;
                toRender.get(i).getSecond().add(new Pair<>(d.getShader(), new ArrayList<>()));
                j++;
            }
            if (!d.getModel().equals(currentModel)) {
                currentModel = d.getModel();
                toRender.get(i).getSecond().get(j).getSecond().add(new Pair<>(d.getModel(), new ArrayList<>()));
                k++;
            }
            toRender.get(i).getSecond().get(j).getSecond().get(k).getSecond().add(d);
        }
    }

    /**
     * Binds the correct shader, sets the projection and renders each object for that shader
     */
    public void render(double interval) {
        for (int i = 0; i < animatables.size(); i++) {
            animatables.get(i).animate(interval);
        }

        // Renders the list in order going by z axis, shader equivalence, model equivalence
        for (int i = 0; i < toRender.size(); i++) {
            var q = toRender.get(i);
            for (int j = 0; j < q.getSecond().size(); j++) {
                var r = q.getSecond().get(j);
                r.getFirst().use();
                r.getFirst().setMatrix4f("projection", camera.getProjection());
                for (int k = 0; k < r.getSecond().size(); k++) {
                    var s = r.getSecond().get(k);
                    modelBindings.get(s.getFirst()).bind();
                    for (int l = 0; l < s.getSecond().size(); l++) {
                        s.getSecond().get(l).draw();
                    }
                }
            }
        }
    }
}
