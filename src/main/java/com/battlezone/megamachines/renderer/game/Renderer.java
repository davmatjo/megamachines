package com.battlezone.megamachines.renderer.game;

import java.util.*;

public class Renderer {

    private Map<Shader, List<AbstractRenderable>> renderables = new LinkedHashMap<>();
    private List<AbstractRenderable> staticRenderables = new ArrayList<>();
    private final Camera camera;

    public Renderer(Camera camera) {
        this.camera = camera;
    }

    public void addRenderable(AbstractRenderable renderable) {
        Shader shader = renderable.getShader();
        if (renderables.containsKey(shader)) {
            renderables.get(shader).add(renderable);
        } else {
            renderables.put(shader, List.of(renderable));
        }
    }

    public void render() {
        renderables.forEach((shader, renderables) -> {
            shader.use();
            shader.setMatrix4f("projection", camera.getProjection());
            for (var renderable : renderables) {
                renderable.render();
            }
        });
//        Shader.STATIC.use();
//        renderStaticElements();
    }

    private void renderStaticElements() {
        for (var renderable : staticRenderables) {
            renderable.render();
        }
    }

    public void addStaticRenderable(AbstractRenderable renderable) {
        staticRenderables.add(renderable);
    }
}
