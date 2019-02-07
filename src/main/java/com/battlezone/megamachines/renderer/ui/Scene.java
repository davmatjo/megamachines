package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Drawable;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.DrawableRenderer;
import com.battlezone.megamachines.renderer.Shader;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    static final Matrix4f STATIC_PROJECTION = new Camera(2 * Window.getWindow().getAspectRatio(), 2f).getProjection();
    private static final Shader shader = Shader.STATIC;
    private final List<Renderable> elements = new ArrayList<>();
    private final List<Interactive> interactives = new ArrayList<>();
    private final Matrix4f identity = new Matrix4f();

    public void render() {
        shader.use();
        shader.setMatrix4f("position", STATIC_PROJECTION);
        for (var drawable : elements) {
            shader.setMatrix4f("texturePosition", identity);
            drawable.render();
        }
        for (var interactive : interactives) {
            interactive.update();
        }
    }

    public void addElement(Drawable drawable) {
        elements.add(new DrawableRenderer(drawable));
        if (drawable instanceof Interactive) {
            interactives.add((Interactive) drawable);
        }
    }

    public void addElement(Renderable renderable) {
        elements.add(renderable);
        if (renderable instanceof Interactive) {
            interactives.add((Interactive) renderable);
        }
    }

    public void removeElement(Renderable renderable) {
        elements.remove(renderable);
    }
}
