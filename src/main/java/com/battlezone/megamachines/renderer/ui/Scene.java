package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.Shader;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private static final float CAM_WIDTH = 2f;
    private static final float CAM_HEIGHT = 2f;
    static final Camera STATIC_CAMERA = new Camera(CAM_WIDTH * Window.getWindow().getAspectRatio(), CAM_HEIGHT);
    private static final Shader shader = Shader.STATIC;
    private final List<Renderable> elements = new ArrayList<>();
    private final List<Interactive> interactives = new ArrayList<>();
    private final Matrix4f identity = new Matrix4f();

    public void render() {
        shader.use();
        shader.setMatrix4f("position", STATIC_CAMERA.getProjection());
        for (var drawable : elements) {
            shader.setMatrix4f("texturePosition", identity);
            drawable.render();
        }
        for (var interactive : interactives) {
            interactive.update();
        }
    }

//    public void addElement(Drawable drawable) {
//        elements.add(new DrawableRenderer(drawable));
//        if (drawable instanceof Interactive) {
//            interactives.add((Interactive) drawable);
//        }
//    }

    public void addElement(Renderable renderable) {
        elements.add(renderable);
        if (renderable instanceof Interactive) {
            interactives.add((Interactive) renderable);
        }
    }

    public boolean isDrawing(Renderable renderable) {
        return elements.contains(renderable);
    }

    public void removeElement(Renderable renderable) {
        elements.remove(renderable);
    }

    public void hide() {
        for (var interactive : interactives) {
            interactive.hide();
        }
    }

    public void show() {
        for (var interactive : interactives) {
            interactive.show();
        }
        Window.getWindow().setResizeCamera(STATIC_CAMERA, CAM_WIDTH, CAM_HEIGHT);
    }
}
