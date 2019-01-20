package com.battlezone.megamachines;

import com.battlezone.megamachines.input.GameInput;
import com.battlezone.megamachines.input.KeyCode;
import com.battlezone.megamachines.renderer.*;
import com.battlezone.megamachines.util.AssetManager;
import com.battlezone.megamachines.world.Track;
import com.battlezone.megamachines.world.TrackType;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class Main {

    private Main() {
        // Attempt to initialise GLFW
        if (!glfwInit()) {
            System.err.println("GLFW Failed to initialise");
            System.exit(-1);
        }

        GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        int width = mode.width();
        int height = mode.height();
        // Create window
        long gameWindow = glfwCreateWindow(width, height, "MegaMachines", glfwGetPrimaryMonitor(), 0);

        glfwSwapInterval(0);

        // Initialise openGL states
        glfwShowWindow(gameWindow);
        glfwMakeContextCurrent(gameWindow);
        GL.createCapabilities(false);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        // Create textures and drawables
        Texture texture1 = AssetManager.loadTexture("/tracks/track_d.png");
        Texture texture2 = AssetManager.loadTexture("/tracks/track_l.png");
        Texture[] textures = {texture1, texture2};
        Shader shader = AssetManager.loadShader("/shaders/entity");
        Model square = Model.generateSquare();
//        Renderer drawable1 = new Renderer(Model.generateSquare(), shader, 100, 0, 200);
        TrackRenderer trackRenderer = new TrackRenderer(square, shader);

//        List<Track> tracks = new ArrayList<>();
//        for (int i = -2000; i < 2000; i += 400) {
//            for (int j = -2000; j < 2000; j += 400) {
////                drawables.add(new Renderer(square, shader, i, j, 200));
//                tracks.add(new Track(new Vector2f(i, j), TrackType.RIGHT));
//            }
//        }
        trackRenderer.setTrack(Track.generateMap(10, 10, 50));

        Camera camera = new Camera((int) (1000 * ((float) width / (float) height)), 1000);
        camera.setPosition(200, 0, 0);

        GameInput gameInput = new GameInput();
        glfwSetKeyCallback(gameWindow, gameInput);

        double previousTime = System.nanoTime();
        double frametime = 0;
        int frames = 0;

        int i = 0;
        int j = 0;
        // Game loop for now
        while (!glfwWindowShouldClose(gameWindow)) {
            glfwPollEvents();

            double currentTime = System.nanoTime();
            double interval = currentTime - previousTime;
            frametime += interval;
            frames += 1;

            previousTime = currentTime;

            if (gameInput.isPressed(KeyCode.D))
                i += 10;
            if (gameInput.isPressed(KeyCode.A))
                i -= 10;
            if (gameInput.isPressed(KeyCode.W))
                j += 10;
            if (gameInput.isPressed(KeyCode.S))
                j -= 10;
            camera.setPosition(i, j, 0);

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

//            shader.setMatrix4f("projection", camera.getProjection().scale(128));
//            drawable1.render(texture1);
            shader.setMatrix4f("projection", camera.getProjection());
//            drawable1.start();
//            texture2.bind();
//            drawables.forEach(Renderer::draw);
//            drawable1.stop();

            trackRenderer.render();

            glfwSwapBuffers(gameWindow);
//            i++;

            if (frametime >= 1000000000) {
                frametime = 0;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }

    public static void main(String[] args) {
//        SharedLibraryLoader.load();
        new Main();

    }
}
