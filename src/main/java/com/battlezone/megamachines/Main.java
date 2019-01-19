package com.battlezone.megamachines;

import com.battlezone.megamachines.renderer.Model;
import com.battlezone.megamachines.renderer.Shader;
import com.battlezone.megamachines.renderer.Sprite;
import com.battlezone.megamachines.renderer.Texture;
import com.battlezone.megamachines.util.AssetManager;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private Main() {
        // Attempt to initialise GLFW
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        // Create window
        long gameWindow = glfwCreateWindow(640, 480, "MegaMachines", 0, 0);

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Create textures and sprites
        Texture texture1 = AssetManager.loadTexture("/tracks/track_d.png");
        Texture texture2 = AssetManager.loadTexture("/tracks/track_l.png");
        Texture[] textures = {texture1, texture2};
        Shader shader = AssetManager.loadShader("/shaders/entity");
        Sprite sprite = new Sprite(Model.generateSquare(), shader);
        int i = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            sprite.render(textures[i % 2]);

            glfwSwapBuffers(gameWindow);
            i++;
        }
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();
    }
}
