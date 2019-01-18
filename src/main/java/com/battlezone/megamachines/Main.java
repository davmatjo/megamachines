package com.battlezone.megamachines;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

import org.lwjgl.opengl.GL;

public class Main {

    private Main() {
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        long gameWindow = glfwCreateWindow(1920, 1080, "MegaMachines", 0, 0);
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);

        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);
            glBegin(GL_QUADS);
            {
                glColor4f(1, 0, 0, 0);
                glVertex2f(-0.5f, -0.5f);
                glColor4f(0, 1, 0, 0);
                glVertex2f(-0.5f, 0.5f);
                glColor4f(1, 0, 1, 0);
                glVertex2f(0.5f, 0.5f);
                glColor4f(1, 0, 0, 0);
                glVertex2f(0.5f, -0.5f);
            }

            glEnd();

            glfwSwapBuffers(gameWindow);
        }
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();
    }
}
