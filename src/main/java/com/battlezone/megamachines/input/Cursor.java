package com.battlezone.megamachines.input;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWCursorPosCallback;

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
//        float xScale =
        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                float width = (windowWidth / 1080f) * 2f;
                float height = 2f;
//                x = new Vector4f((float) xpos, 0, 0 ,0).mul(new Matrix4f().ortho2D(-width / 2, width / 2, height / 2, -height / 2).invert()).x / (1920f);
//                x = new Vector4f((float) xpos, 0, 0 ,0).mul(new Matrix4f().ortho2D(-width / 2, width / 2, height / 2, -height / 2)).x / (1920f);
                x = 2 * ((float) xpos / windowWidth - GL_OFFSET) / ((float) windowHeight / windowWidth);
                y = 2 * (((float)windowHeight - ypos) / windowHeight - GL_OFFSET);
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
