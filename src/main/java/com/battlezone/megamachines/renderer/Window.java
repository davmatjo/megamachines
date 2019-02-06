package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.input.Cursor;
import com.battlezone.megamachines.input.Gamepad;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class Window {

    private static Window window = null;
    private final long gameWindow;
    private final float aspectRatio;
    private int width = 1920;
    private int height = 1080;

    public static Window getWindow() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    private Window() {
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Create window
        gameWindow = glfwCreateWindow(width, height, "MegaMachines", 0, 0);

        Cursor cursor = new Cursor(gameWindow, width, height);

        glfwSwapInterval(1);

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        aspectRatio = (float) width / (float) height;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public long getGameWindow() {
        return gameWindow;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
