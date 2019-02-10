package com.battlezone.megamachines.input;

import com.battlezone.megamachines.events.mouse.MouseButtonEvent;
import com.battlezone.megamachines.messaging.MessageBus;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Class for managing cursor related updates
 * @author David
 */
public class Cursor {

    private static final float GL_OFFSET = 0.5f;
    private final long window;
    private double x = 0.0;
    private double y = 0.0;

    public Cursor(long window, int windowWidth, int windowHeight) {
        this.window = window;
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                x = 2 * ((float) xpos / windowWidth - GL_OFFSET) / ((float) windowHeight / windowWidth);
                y = 2 * (((float)windowHeight - ypos) / windowHeight - GL_OFFSET);
            }
        });
        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                MessageBus.fire(new MouseButtonEvent(button, action));
            }
        });
    }

    public void enable() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void disable() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void update() {
//        glfwGetCursorPos(window, x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
