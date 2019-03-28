package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import com.battlezone.megamachines.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class for managing cursor related updates
 */
public class Cursor {

    private static final float GL_OFFSET = 0.5f;
    private static Cursor cursor;
    private final long window;
    private double x = 0.0;
    private double y = 0.0;

    /**
     * Creates a cursor for a given window
     *
     * @param window window ID assigned by GLFW
     */
    private Cursor(long window) {
        this.window = window;
        glfwSetCursorPosCallback(window, (windowz, xpos, ypos) -> {
            int windowWidth = Window.getWindow().getWidth();
            int windowHeight = Window.getWindow().getHeight();
            x = 2 * ((float) xpos / windowWidth - GL_OFFSET) / ((float) windowHeight / windowWidth);
            y = 2 * (((float) windowHeight - ypos) / windowHeight - GL_OFFSET);
        });
        glfwSetMouseButtonCallback(window, (windowz, button, action, mods) ->
                MessageBus.fire(new MouseButtonEvent(button, action)));
    }

    /**
     * Get the singleton instance of the Cursor class
     *
     * @return an instance
     */
    public static Cursor getCursor() {
        if (cursor == null) {
            cursor = new Cursor(Window.getWindow().getGameWindow());
        }
        return cursor;
    }

    /**
     * Shows the cursor
     */
    public void enable() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    /**
     * Hides the cursor
     */
    public void disable() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }


    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
