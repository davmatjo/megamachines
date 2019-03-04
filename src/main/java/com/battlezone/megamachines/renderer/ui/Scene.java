package com.battlezone.megamachines.renderer.ui;

import com.battlezone.megamachines.events.ui.ErrorEvent;
import com.battlezone.megamachines.math.Matrix4f;
import com.battlezone.megamachines.messaging.EventListener;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Renderable;
import com.battlezone.megamachines.renderer.Window;
import com.battlezone.megamachines.renderer.game.Camera;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.ui.elements.Box;
import com.battlezone.megamachines.renderer.ui.elements.Label;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scene {

    public static final float CAM_WIDTH = 2f;
    public static final float CAM_HEIGHT = 2f;
    public static final Camera STATIC_CAMERA = new Camera(CAM_WIDTH * Window.getWindow().getAspectRatio(), CAM_HEIGHT);
    private static final Shader shader = Shader.STATIC;
    private static final float ERROR_WIDTH = 1.5f;
    private static final float ERROR_HEIGHT = 0.35f;
    private static final float ERROR_X = 0.25f;
    private static final float ERROR_Y = -0.9f;
    private static final float BODY_HEIGHT = 0.1f;
    private static final float BODY_X = ERROR_X + 0.02f;
    private static final float BODY_Y = ERROR_Y + 0.05f;
    private static final float TITLE_HEIGHT = 0.1f;
    private static final float TITLE_X = BODY_X;
    private static final float TITLE_Y = BODY_Y + 0.15f;


    private final List<Renderable> elements = new ArrayList<>();
    private final List<Interactive> interactives = new ArrayList<>();
    private final Queue<ErrorEvent> errorEvents = new LinkedList<>();
    private Box errorBox;
    private Label errorTitle;
    private Label errorBody;
    private ErrorEvent lastError;
    private final Matrix4f identity = new Matrix4f();

    private boolean active;

    public Scene() {
        MessageBus.register(this);
        errorBox = new Box(ERROR_WIDTH, ERROR_HEIGHT, ERROR_X, ERROR_Y, Colour.RED);
    }

    public void render() {
        shader.use();
        shader.setMatrix4f("position", STATIC_CAMERA.getProjection());
        for (int i=0; i<elements.size(); i++) {
            shader.setMatrix4f("texturePosition", identity);
            elements.get(i).render();
        }
        for (var interactive : interactives) {
            interactive.update();
        }
        renderErrors();
    }

    private void renderErrors() {
        ErrorEvent event = errorEvents.peek();
        if (event != null) {
            if (!event.isExpired()) {
                if (lastError == event) {
                    errorBox.render();
                    errorTitle.render();
                    errorBody.render();
                    event.increment();
                } else {
                    lastError = event;
                    if (errorTitle != null) {
                        errorTitle.delete();
                    }
                    if (errorBody != null) {
                        errorBody.delete();
                    }
                    if (errorBox != null) {
                        errorBox.delete();
                    }
                    float width = Math.max(Label.getWidth(event.getTitle(), TITLE_HEIGHT), Label.getWidth(event.getMessage(), BODY_HEIGHT)) + 0.08f;
                    float x = -(width / 2);
                    errorBox = new Box(width, ERROR_HEIGHT, x, ERROR_Y, Colour.RED);
                    errorTitle = new Label(event.getTitle(), TITLE_HEIGHT, x + 0.04f, TITLE_Y);
                    errorBody = new Label(event.getMessage(), BODY_HEIGHT, x + 0.04f, BODY_Y);
                }
            } else {
                errorEvents.poll();
            }
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
        active = false;
        for (var interactive : interactives) {
            interactive.hide();
        }
    }

    public void show() {
        active = true;
        for (var interactive : interactives) {
            interactive.show();
        }
        Window.getWindow().setResizeCamera(STATIC_CAMERA, CAM_WIDTH, CAM_HEIGHT);
    }

    @EventListener
    public void showError(ErrorEvent event) {
        if (active) {
            errorEvents.add(event);
        }
    }
}
