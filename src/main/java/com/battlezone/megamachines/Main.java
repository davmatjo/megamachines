package com.battlezone.megamachines;

import static org.lwjgl.glfw.GLFW.*;

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

        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            glfwSwapBuffers(gameWindow);
        }
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();
    }
}
