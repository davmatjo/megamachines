package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.Main;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.game.Renderable;
import com.battlezone.megamachines.renderer.game.Shader;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    public static final Matrix4f STATIC_PROJECTION = new Camera(2 * Main.aspectRatio, 2f).getProjection();
    private static final Shader shader = Shader.STATIC;
    private final List<Renderable> elements = new ArrayList<>();

    public void render() {
        shader.use();
        shader.setMatrix4f("position", STATIC_PROJECTION);
        for (var drawable : elements) {
            drawable.render();
        }
    }

    public void addElement(Renderable drawable) {
        elements.add(drawable);
    }

    public void removeElement(Renderable drawable) {
        elements.remove(drawable);
    }
}
